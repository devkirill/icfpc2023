package icfpc.y2023.controller

import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import icfpc.y2023.service.CalcMetric
import icfpc.y2023.service.LoadProblemsService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class MainController(
    val calcMetric: CalcMetric,
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository,
    val loadProblemsService: LoadProblemsService
) {
    @GetMapping("/problem/get")
    @ResponseBody
    fun getProblems() =
        problemRepository.findAll().sortedBy { it.id }

    @GetMapping("/problem/get/{id}")
    @ResponseBody
    fun getProblem(@PathVariable id: Int) =
        problemRepository.getReferenceById(id)

    @GetMapping("/solution/get/{id}")
    @ResponseBody
    fun getSolution(@PathVariable id: Int) =
        solutionRepository.getReferenceById(id)

    @GetMapping("/best/{id}/{limit}")
    @ResponseBody
    fun getSolutions(@PathVariable id: Int, @PathVariable limit: Int) =
        solutionRepository.findBest(getProblem(id), limit)

    @GetMapping("/best/{id}")
    @ResponseBody
    fun getBestSolution(@PathVariable id: Int) =
        solutionRepository.findBest(getProblem(id))

//    @PostMapping("/problem/add/{id}")
//    @ResponseBody
//    fun addProblem(@PathVariable id: Int) =
//        problemRepository.save(Problem(id)).apply {
//            addSolution(id, solution = "", calc = true)
//        }

    @GetMapping("/problems/read")
    @ResponseBody
    fun readProblems() {
        loadProblemsService.loadProblems()
    }

    @PostMapping("/add/{id}")
    @ResponseBody
    fun addSolution(@PathVariable id: Int, @RequestBody solution: String, calc: Boolean = false) =
        Solution(problem = getProblem(id), contents = solution).apply {
            if (calc) {
                score = calcMetric.calcMetric(this)
            }
        }.let {
            solutionRepository.save(it)
        }
}
