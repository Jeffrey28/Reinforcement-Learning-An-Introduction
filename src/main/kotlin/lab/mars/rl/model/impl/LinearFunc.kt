package lab.mars.rl.model.impl

import lab.mars.rl.model.State
import lab.mars.rl.model.ValueFunction
import lab.mars.rl.util.matrix.Matrix
import lab.mars.rl.util.matrix.times
import org.apache.commons.math3.util.FastMath.*

interface featureFunc {
    operator fun invoke(s: State): Matrix
    fun alpha(alpha: Double, s: State): Double = alpha
    val featureNum: Int
}

operator fun DoubleArray.times(elements: DoubleArray): Double {
    var result = 0.0
    for (i in 0..lastIndex)
        result += this[i] * elements[i]
    return result
}

class SimplePolynomial(override val featureNum: Int, val scale: Double) : featureFunc {
    override fun invoke(s: State) = Matrix.column(featureNum) {
        pow(s[0].toDouble() * scale, it)
    }
}

class SimpleFourier(override val featureNum: Int, val scale: Double) : featureFunc {
    override fun invoke(s: State) = Matrix.column(featureNum) {
        cos(it * PI * s[0].toDouble() * scale)
    }
}

class LinearFunc(val x: featureFunc, val alpha: Double) : ValueFunction {
    override fun `∇`(s: State): Matrix {
        TODO("not implemented")
    }

    val w = Matrix.column(x.featureNum)

    override fun invoke(s: State) = (w.T * x(s)).asScalar()

    override fun update(s: State, delta: Double) {
        val alpha = x.alpha(alpha, s)
        w += alpha * delta * x(s)
    }
}