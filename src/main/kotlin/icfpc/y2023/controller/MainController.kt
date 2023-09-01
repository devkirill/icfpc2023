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
    fun index(): RedirectView {
        return RedirectView("/index.html")
    }

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
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
        response.setHeader("Access-Control-Allow-Origin", "*")
        val image = BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB)
        val solution = solutionRepository.getReferenceById(id)
        val content = problemContentRepository.getReferenceById(solution.problemId).content

        val size = max(content.stage_width, content.stage_height)
        val center = Point(
            content.stage_bottom_left[0] + content.stage_width / 2,
            content.stage_bottom_left[1] + content.stage_height / 2
        )

        val g = image.graphics as Graphics2D

        g.color = Color(255, 0, 255, 0)
        g.fillRect(0, 0, 1000, 1000)

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g.color = Color.WHITE
        var from = (center - Point(content.stage_width, content.stage_height) / 2.0) * 1000.0 / size
        from += (Point(1000.0, 1000.0) - Point(content.stage_width, content.stage_height) * 1000.0 / size) / 2.0
        g.fillRect(
            from.x.toInt(),
            from.y.toInt(),
            (content.stage_width * 1000.0 / size).toInt(),
            (content.stage_height * 1000.0 / size).toInt(),
        )

        val rd = 10 * 1000.0 / size
        g.color = Color.CYAN.darker().darker()
        solution.contents.placements.forEach {
            val a = (it - center) * 1000.0 / size + Point(500.0, 500.0)
            val shape = Ellipse2D.Double(a.x - rd, a.y - rd, rd, rd)
            g.fill(shape)
        }

        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "PNG", baos)
        baos.toByteArray()

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(baos.toByteArray())
    }
}
