package coordinates.validator

import coordinates.exceptions.PointOutOfBoundariesException
import coordinates.geometry.Point2DR
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class GeometryValidatorTest {

    private lateinit var validator: GeometryValidator

    @BeforeEach
    fun setUp() {
        validator = GeometryValidator()
    }

    @Test
    fun `validate - wrong radius - should throw IllegalArgumentException`() {
        val point = Point2DR(0f, 0f, 3f);

        assertThrows<IllegalArgumentException> {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - origin - should pass`() {
        val point = Point2DR(x = 0f, y = 0f, R = 1f)

        assertDoesNotThrow {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - Quadrant I - should pass`() {
        val point = Point2DR(x = 0.2f, y = 0.5f, R = 1f)
        assertDoesNotThrow { validator.validate(point) }
    }

    @Test
    fun `validate - Quadrant I - should throw PointOutOfBoundariesException`() {
        val point = Point2DR(x = 0.6f, y = 0.5f, R = 1f)

        assertThrows<PointOutOfBoundariesException> {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - Quadrant II - should pass`() {
        val point = Point2DR(x = -0.5f, y = 0.5f, R = 1f)

        assertDoesNotThrow {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - Quadrant II - should throw PointOutOfBoundariesException`() {
        val point = Point2DR(x = -0.1f, y = 1.0f, R = 1f)

        assertThrows<PointOutOfBoundariesException> {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - Quadrant III - should pass`() {
        val point = Point2DR(x = -0.2f, y = -0.2f, R = 1f)

        assertDoesNotThrow {
            validator.validate(point)
        }
    }

    @Test
    fun `validate - Quadrant III - should throw PointOutOfBoundariesException`() {
        val point = Point2DR(x = -2.2f, y = -2.2f, R = 1f)

        assertThrows<PointOutOfBoundariesException> {
            validator.validate(point)
        }
    }
}