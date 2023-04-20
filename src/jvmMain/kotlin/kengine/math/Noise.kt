package kengine.math

import kengine.util.terminateError
import kotlin.math.pow

object Noise {
    var seed = System.nanoTime()

    inline fun <reified S : Size> simplex(v: Vector<S, *, *>) = when (S::class) {
        Two::class -> OpenSimplex2.noise2_ImproveX(seed, v[0].toDouble(), v[1].toDouble())
        Three::class -> OpenSimplex2.noise3_ImproveXY(seed, v[0].toDouble(), v[1].toDouble(), v[2].toDouble())
        Four::class -> OpenSimplex2.noise4_ImproveXYZ(
            seed, v[0].toDouble(), v[1].toDouble(), v[2].toDouble(), v[3].toDouble()
        )
        else -> terminateError("Unexpected Vector class ${v::class}")
    }

    inline fun <reified S : Size, reified V : Vector<S, Float, *>> fractal(octaves: Int, persistence: Float, v: V) =
        (1..octaves).map { i -> simplex((v * 2f.pow(i)) as V) * persistence.pow(i) }.sum()
}