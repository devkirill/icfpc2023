package icfpc.y2023

import icfpc.y2023.model.Point
import icfpc.y2023.service.CalcScoringService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Intersect {
    val scoring = CalcScoringService()

    @Test
    fun test() {
        val a = Point(0.0, 0.0)
        val b = Point(100.0, 100.0)
        assertTrue(scoring.intersect(a, b, Point(50.0, 50.0)))
        assertTrue(scoring.intersect(a, b, Point(52.5, 50.0)))
        assertTrue(scoring.intersect(a, b, Point(47.5, 50.0)))
    }

    @Test
    fun genImg() {
        val img = BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB)

        val a = Point(50.0, 50.0) / 10.0
        val b = Point(950.0, 950.0) / 10.0
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                val m = Point(x.toDouble(), y.toDouble()) / 10.0
                if (scoring.intersect(a, b, m)) {
                    img.setRGB(x, y, Color.BLACK.rgb)
                } else {
                    img.setRGB(x, y, Color.WHITE.rgb)
                }
            }
        }

        ImageIO.write(img, "PNG", File("test.png"))
    }
}
