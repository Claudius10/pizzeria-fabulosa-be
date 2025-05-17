package org.pizzeria.fabulosa.web.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerUtilsTest {

	@Test
	void givenOneNull_returnTwo() {
		String one = null;
		String two = "test2";

		String s = ServerUtils.resolvePath(one, two);

		assertThat(s).isEqualTo("test2");
	}

	@Test
	void givenTwoNull_returnOne() {
		String one = "test1";
		String two = null;

		String s = ServerUtils.resolvePath(one, two);

		assertThat(s).isEqualTo("test1");
	}

	@Test
	void givenOneAndTwoOK_returnOne() {
		String one = "test1";
		String two = "test2";

		String s = ServerUtils.resolvePath(one, two);

		assertThat(s).isEqualTo("test1");
	}

	@Test()
	void givenAllNull_thenThrow() {
		String path = ServerUtils.resolvePath(null, null);
		assertThat(path).isNull();
	}
}
