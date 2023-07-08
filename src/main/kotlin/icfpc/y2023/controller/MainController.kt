package icfpc.y2023.controller

import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.model.Solve
import icfpc.y2023.model.Task
import icfpc.y2023.service.CalcScoringService
import icfpc.y2023.service.LoadProblemsService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class MainController(
    val calcMetric: CalcScoringService,
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository,
    val loadProblemsService: LoadProblemsService
) {
//    @GetMapping("/problem/get")
//    @ResponseBody
//    fun getProblems() =
//        problemRepository.findAll().sortedBy { it.id }

    //    @GetMapping("/problem/get/{id}", produces = ["application/json"])
//    @ResponseBody
    fun getProblem(id: Int): Problem {
        return problemRepository.getReferenceById(id)
    }


    @GetMapping("/problem/{id}", produces = ["application/json"])
    @ResponseBody
    fun getProblemTask(@PathVariable id: Int, response: HttpServletResponse): Task {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return getProblem(id).problem
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
        response: HttpServletResponse
    ): Solution {
        response.setHeader("Access-Control-Allow-Origin", "*")
        return Solution(problem = getProblem(id), contents = Solve.parse(solution)).apply {
            if (calc) {
                score = calcMetric.calc(this)
            }
        }.let {
            solutionRepository.save(it)
        }
    }
}
