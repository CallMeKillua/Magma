package com.meti;

public class NativeResults {
    public static <T, E extends Throwable> Result<T, E> $throwResult(ValueAction<Result<T, E>, E> action) {
        try {
            return action.run();
        } catch (Throwable e) {
            return new Err<>((E) e);
        }
    }

    public static <T, E extends Throwable> Result<T, E> $throw(ValueAction<T, E> action) {
        try {
            return Ok.apply(action.run());
        } catch (Throwable e) {
            return new Err<>((E) e);
        }
    }

    public static <T, E extends Throwable> T $(Result<T, E> result) throws E {
        return result.unwrap();
    }

    public interface ValueAction<T, E extends Throwable> {
        T run() throws E;
    }
}
