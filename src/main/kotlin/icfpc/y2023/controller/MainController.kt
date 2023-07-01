package icfpc.y2023.controller

import icfpc.y2023.CalcMetric
import icfpc.y2023.db.model.Problem
import icfpc.y2023.db.model.Solution
import icfpc.y2023.db.repository.ProblemRepository
import icfpc.y2023.db.repository.SolutionRepository
import icfpc.y2023.db.repository.findBest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class MainController(
    val calcMetric: CalcMetric,
    val problemRepository: ProblemRepository,
    val solutionRepository: SolutionRepository
) {
    @GetMapping("/problem/get")
    @ResponseBody
    fun getProblems() =
        problemRepository.findAll().sortBy { it.id }

    @GetMapping("/problem/get/{id}")
    @ResponseBody
    fun getProblem(@PathVariable id: Long) =
        problemRepository.getReferenceById(id)

    @GetMapping("/solution/get/{id}")
    @ResponseBody
    fun getSolution(@PathVariable id: Long) =
        solutionRepository.getReferenceById(id)

    @GetMapping("/best/{id}")
    @ResponseBody
    fun getBestSolution(@PathVariable id: Long) =
        solutionRepository.findBest(getProblem(id))

    @PostMapping("/problem/add/{id}")
    @ResponseBody
    fun addProblem(@PathVariable id: Long) =
        problemRepository.save(Problem(id))

    @PostMapping("/add/{id}")
    @ResponseBody
    fun addSolution(@PathVariable id: Long, @RequestBody solution: String, calc: Boolean = false) =
        Solution(problem = getProblem(id), solution = solution).apply {
            if (calc) {
                score = calcMetric.calcMetric(this)
            }
        }.let {
            solutionRepository.save(it)
        }
}
