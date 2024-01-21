package utils;

import io.github.robertomike.hefesto.exceptions.QueryException;
import io.github.robertomike.hefesto.utils.CastUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CastUtilsTest {


    static Stream<Arguments> castValue() {
        return Stream.of(
                Arguments.of(int.class, 1),
                Arguments.of(long.class, 1),
                Arguments.of(byte.class, 1),
                Arguments.of(short.class, 1),
                Arguments.of(double.class, 1),
                Arguments.of(char.class, 1),
                Arguments.of(Long.class, 1),
                Arguments.of(Integer.class, 1),
                Arguments.of(Short.class, 1),
                Arguments.of(Byte.class, 1),
                Arguments.of(Double.class, 1),
                Arguments.of(BigDecimal.class, 1),
                Arguments.of(BigInteger.class, 1),
                Arguments.of(Boolean.class, 1),
                Arguments.of(Long.class, 1L),
                Arguments.of(String.class, 1L)
        );
    }

    @ParameterizedTest
    @MethodSource("castValue")
    void castValue(Class<?> type, Object value) {
        var result = CastUtils.castValue(type, value);

        assertNotNull(result);
    }

    @Test
    void unsupportedCastingType() {
        assertThrows(
                QueryException.class,
                () -> CastUtils.castValue(ArrayList.class, 1L)
        );
    }
}