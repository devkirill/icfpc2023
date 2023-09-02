package icfpc.y2023.controller

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemContentRepository
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.model.Point
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.service.LoadProblemsService
import icfpc.y2023.service.UploadService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.lang.Double.max
import javax.imageio.ImageIO


@Controller
class MainController(
    val calcMetric: CalcScoringService,
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository,
    val loadProblemsService: LoadProblemsService,
    val problemContentRepository: ProblemContentRepository,
    val uploadService: UploadService
) {
    @GetMapping("/")
    fun index(): String = "index"

    @GetMapping("/problems")
    @ResponseBody
    fun getProblems(response: HttpServletResponse): List<Problem> {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return problemRepository.findAll().sortedBy { it.id }
    }

    //    @GetMapping("/problem/get/{id}", produces = ["application/json"])
//    @ResponseBody
    fun getProblem(id: Int): Problem {
        return problemRepository.getReferenceById(id)
    }


    @GetMapping("/problem/{id}", produces = ["application/json"])
    @ResponseBody
    fun getProblemTask(@PathVariable id: Int, response: HttpServletResponse): Task {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val contentId = getProblem(id).problemId
        return problemContentRepository.getReferenceById(contentId).content
    }

    @GetMapping("/solution/get/{id}", produces = ["application/json"])
    @ResponseBody
    fun getSolution(@PathVariable id: Int, response: HttpServletResponse): Solution {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return solutionRepository.getReferenceById(id)
    }


    @GetMapping("/best/{id}/{limit}", produces = ["application/json"])
    @ResponseBody
    fun getSolutions(@PathVariable id: Int, @PathVariable limit: Int, response: HttpServletResponse): List<Solution> {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return solutionRepository.findBest(getProblem(id), limit)
    }


    @GetMapping("/best/{id}", produces = ["application/json"])
    @ResponseBody
    fun getBestSolution(@PathVariable id: Int, response: HttpServletResponse): Solution? {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return solutionRepository.findBest(getProblem(id))
    }


//    @PostMapping("/problem/add/{id}")
//    @ResponseBody
//    fun addProblem(@PathVariable id: Int) =
//        problemRepository.save(Problem(id)).apply {
//            addSolution(id, solution = "", calc = true)
//        }

    @GetMapping("/problems/read", produces = ["application/json"])
    @ResponseBody
    fun readProblems(response: HttpServletResponse) {
        response.setHeader("Access-Control-Allow-Origin", "*")
        loadProblemsService.loadProblems()
    }

    @PostMapping("/add/{id}", produces = ["application/json"])
    @ResponseBody
    fun addSolution(
        @PathVariable id: Int,
        @RequestBody solution: String,
        calc: Boolean = false,
        upload: Boolean = false,
        response: HttpServletResponse
    ): Solution {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val solution = Solution(problem = getProblem(id), contents = Solve.parse(solution)).apply {
            if (calc) {
                val contentId = getProblem(id).problemId
                score = calcMetric.calc(problemContentRepository.getReferenceById(contentId).content, this.contents)
            }
        }.let {
            solutionRepository.save(it)
        }
        if (upload) {
            uploadService.upload(solution)
        }
        return solution
    }

    @GetMapping("/img/{id}")
    @ResponseBody
    fun getImage(
        @PathVariable id: Int,
        imgSize: Int?,
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val solution = solutionRepository.getReferenceById(id)
        val content = problemContentRepository.getReferenceById(solution.problemId).content

        val size = max(content.stage_width, content.stage_height) * 1.05
        val center = Point(
            content.stage_bottom_left[0] + content.stage_width / 2,
            content.stage_bottom_left[1] + content.stage_height / 2
        )
        val image = ImageDraw(imgSize ?: 1000, center, size) {
            color = Color.LIGHT_GRAY
            fillRect(
                Point(content.stage_bottom_left[0], content.stage_bottom_left[1]),
                content.stage_width,
                content.stage_height,
            )
            color = Color.BLACK
            drawRect(
                Point(content.stage_bottom_left[0], content.stage_bottom_left[1]),
                content.stage_width,
                content.stage_height,
            )

            solution.contents.placements.forEachIndexed { index, it ->
                color = Color.getHSBColor(
                    content.musicians[index].toFloat() / content.musicians.max(),
                    0.5F,
                    0.5F
                )
                fillCircle(it, 5.0)
            }

            color = Color.LIGHT_GRAY

            content.pillars.forEach { pillar  ->
                fillCircle(Point(pillar.center[0], pillar.center[1]), pillar.radius)
            }

            color = Color.MAGENTA.darker()

            content.attendees.forEach { attendee ->
                fillCircle(Point(attendee.x, attendee.y), 5.0)
            }
        }

        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", baos)
        baos.toByteArray()

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(baos.toByteArray())
    }

    @GetMapping("/img2/{id}")
    @ResponseBody
    fun getImage2(
        @PathVariable id: Int,
        imgSize: Int? = null,
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val solution = solutionRepository.getReferenceById(id)
        val content = problemContentRepository.getReferenceById(solution.problemId).content

        val size = max(content.attendees.map { it.x }.max() + 5.0, content.attendees.map { it.y }.max() + 5.0)
        val center = Point(
            (content.attendees.map { it.x }.max() + 5.0) / 2,
            (content.attendees.map { it.y }.max() + 5.0) / 2
        )
        val image = ImageDraw(imgSize ?: 1000, center, size) {
            color = Color.LIGHT_GRAY
            fillRect(
                Point(content.stage_bottom_left[0], content.stage_bottom_left[1]),
               content.stage_width,
                content.stage_height,
            )
            color = Color.BLACK
            drawRect(
                Point(content.stage_bottom_left[0], content.stage_bottom_left[1]),
                content.stage_width,
                content.stage_height,
            )

            solution.contents.placements.forEachIndexed { index, it ->
                color = Color.getHSBColor(
                    content.musicians[index].toFloat() / content.musicians.max(),
                    0.5F,
                    0.5F
                )
                fillCircle(it, 5.0)
            }

            color = Color.LIGHT_GRAY

            content.pillars.forEach { pillar  ->
                fillCircle(Point(pillar.center[0], pillar.center[1]), pillar.radius)
            }

            color = Color.MAGENTA.darker()

            content.attendees.forEach { attendee ->
                fillCircle(Point(attendee.x, attendee.y), 5.0)
            }
        }

        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", baos)
        baos.toByteArray()

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(baos.toByteArray())
    }
}

data class ImageDraw(val size: Int, val center: Point, val scale: Double, val image: BufferedImage) {
    val g2d = image.graphics as Graphics2D

    var color: Color
        get() {
            TODO()
        }
        set(value) {
            g2d.color = value
        }

    init {
        color = Color(255, 0, 255, 0)
        g2d.fillRect(0, 0, size, size)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }

    companion object {
        operator fun invoke(size: Int, center: Point, scale: Double, draw: ImageDraw.() -> Unit): BufferedImage {
            val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            val drawI = ImageDraw(size, center, scale, image)
            drawI.draw()
            return drawI.image
        }
    }

    infix fun Point.pMul(a: Point) = Point(this.x * a.x, this.y * a.y)
    infix fun Point.pDiv(a: Point) = Point(this.x / a.x, this.y / a.y)

    fun convert(p: Point) = ((p - center) * size.toDouble() / scale) + Point(size.toDouble() / 2, size.toDouble() / 2)
    fun convert(d: Double) = d * size.toDouble() / scale

    fun fillCircle(p: Point, r: Double) {
        val a = convert(p)
        val rd = convert(r)

        val shape = Ellipse2D.Double(a.x - rd, a.y - rd, rd * 2, rd * 2)
        g2d.fill(shape)
    }

    fun fillRect(from: Point, width: Double, height: Double) {
        val a = convert(from)
        g2d.fillRect( a.x.toInt(), a.y.toInt(), convert(width).toInt(),  convert(height).toInt())
    }

    fun drawRect(from: Point, width: Double, height: Double) {
        val a = convert(from)
        g2d.drawRect( a.x.toInt(), a.y.toInt(), convert(width).toInt(),  convert(height).toInt())
    }
}
