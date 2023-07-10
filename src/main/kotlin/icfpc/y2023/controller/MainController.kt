package icfpc.y2023.controller

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemContentRepository
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.service.LoadProblemsService
import icfpc.y2023.service.UploadService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

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
}
