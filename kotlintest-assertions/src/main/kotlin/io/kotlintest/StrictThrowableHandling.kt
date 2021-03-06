package io.kotlintest


/**
 * Verifies that a block of code throws a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code to verify if it throws a specific throwable [T], when [shouldThrowExactly]
 * can't be used for whatever reason, such as assignment operations (assignments are statements therefore has no return
 * value).
 *
 * This function will exclude subclasses of [T]. For example, if you test for [java.io.IOException] and the code block
 * throws [java.io.FileNotFoundException], the test will fail, as [java.io.FileNotFoundException] is a subclass of
 * [java.io.IOException], but not exactly [java.io.IOException].
 *
 * If you wish to include any subclasses, you should use [shouldThrowUnit] instead.
 *
 * If you don't care about the thrown type at all, use [shouldThrowAnyUnit] instead.
 *
 * ```
 *     val thrown: FooException = shouldThrowExactlyUnit<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrowExactly]
 */
inline fun <reified T : Throwable> shouldThrowExactlyUnit(block: () -> Unit): T = shouldThrowExactly { block() }

/**
 * Verifies that a block of code throws a Throwable of type [T], not including subclasses of [T]
 *
 * Use this function to wrap a block of code to verify if it throws a specific throwable [T]
 *
 * This function will exclude subclasses of [T]. For example, if you test for [java.io.IOException] and the code block
 * throws [java.io.FileNotFoundException], the test will fail, as [java.io.FileNotFoundException] is a subclass of
 * [java.io.IOException], but not exactly [java.io.IOException].
 *
 * If you wish to include any subclasses, you should use [shouldThrow] instead.
 *
 * If you don't care about the thrown type at all, use [shouldThrowAny] instead.
 *
 *
 *
 * **Attention to assignment operations**:
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment throws a [Throwable], use [shouldThrowExactlyUnit] or it's variations.
 *
 * ```
 *     val thrown: FooException = shouldThrowExactlyUnit<FooException> {
 *         // Code that we expect to throw FooException
 *         throw FooException()
 *     }
 * ```
 *
 * @see [shouldThrowExactly]
 */
inline fun <reified T : Throwable> shouldThrowExactly(block: () -> Any?): T {
  val expectedExceptionClass = T::class
  val thrownThrowable = try {
    block()
    null  // Can't throw Failures.failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
  } catch (thrown: Throwable) { thrown  }

  return when {
    thrownThrowable == null -> throw Failures.failure("Expected exception ${T::class.qualifiedName} but no exception was thrown.")
    thrownThrowable::class == expectedExceptionClass -> thrownThrowable as T  // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
    thrownThrowable is AssertionError -> throw thrownThrowable
    else -> throw Failures.failure("Expected exception ${expectedExceptionClass.qualifiedName} but a ${thrownThrowable::class.simpleName} was thrown instead.", thrownThrowable)
  }

}