package org.lmrl.fp;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Maybe<T> implements Iterable<T> {
    private Maybe() {
    }

    public static <T> Maybe<T> of(T value) {
        return value == null ? none() : some(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Maybe<T> fromOptional(Optional<T> op) {
        Objects.requireNonNull(op);
        return op.map(Maybe::some).orElseGet(Maybe::none);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> T getOptionOrThrow(Optional<T> op) throws Exception {
        Objects.requireNonNull(op);
        return op.orElseThrow(Exception::new);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> T getOptionOrThrow(Optional<T> op, Exception t) throws Exception {
        Objects.requireNonNull(op);
        return op.orElseThrow(() -> t);
    }

    public static <T> Maybe<T> some(T value) {
        return Inner.some(value);
    }

    public static <T> Maybe<T> none() {
        return Inner.none();
    }

    public Optional<T> toOptional() {
        return andThen(Optional::of).orElse(Optional.empty());
    }

    public abstract boolean isPresent();

    public abstract T get() throws Exception;

    public abstract <U> Maybe<U> andThen(Function<T, U> action);

    public abstract <U> Maybe<U> andThenMaybe(Function<T, Maybe<U>> action);

    public abstract T orElse(T other);

    public abstract T orElseGet(Supplier<T> other);

    public abstract Maybe<T> orElseMaybe(Supplier<Maybe<T>> other);

    public abstract void action(Consumer<T> cons);

    public abstract Iterator<T> iterator();

    private static class Inner {

        private static final None<?> none = new None<>();

        @SuppressWarnings("unchecked")
        static <T> Maybe<T> none() {
            return (Maybe<T>) none;
        }

        static <T> Maybe<T> some(T value) {
            Objects.requireNonNull(value);
            return new Inner.Some<>(value);
        }

        private static class Some<T> extends Maybe<T> {

            T value;

            Some(T value) {
                this.value = value;
            }

            @Override
            public boolean isPresent() {
                return true;
            }

            @Override
            public T get() {
                return value;
            }

            @Override
            public <U> Maybe<U> andThen(Function<T, U> action) {
                return Inner.some(action.apply(value));
            }

            @Override
            public <U> Maybe<U> andThenMaybe(Function<T, Maybe<U>> action) {
                return action.apply(value);
            }

            @Override
            public T orElse(T other) {
                return value;
            }

            @Override
            public T orElseGet(Supplier<T> other) {
                return value;
            }

            @Override
            public Maybe<T> orElseMaybe(Supplier<Maybe<T>> other) {
                return this;
            }

            @Override
            public void action(Consumer<T> action) {
                action.accept(value);
            }


            @Override
            public Iterator<T> iterator() {
                return new Iterator<>() {
                    private boolean yielded = false;

                    @Override
                    public boolean hasNext() {
                        return !yielded;
                    }

                    @Override
                    public T next() {
                        if (yielded) {
                            throw new NoSuchElementException();
                        }
                        yielded = true;
                        return value;
                    }
                };
            }

            @Override
            public int hashCode() {
                return value.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Some<?> && value.equals(((Some<?>) obj).value);
            }

            @Override
            public String toString() {
                return value.toString();
            }
        }

        private static class None<T> extends Maybe<T> {
            @Override
            public boolean isPresent() {
                return false;
            }

            @Override
            public T get() throws Exception {
                throw new Exception();
            }

            @Override
            public <U> Maybe<U> andThen(Function<T, U> action) {
                return Maybe.none();
            }

            @Override
            public <U> Maybe<U> andThenMaybe(Function<T, Maybe<U>> action) {
                return Maybe.none();
            }

            @Override
            public T orElse(T other) {
                return other;
            }

            @Override
            public T orElseGet(Supplier<T> other) {
                return other.get();
            }

            @Override
            public Maybe<T> orElseMaybe(Supplier<Maybe<T>> other) {
                return other.get();
            }

            @Override
            public void action(Consumer<T> cons) {
            }

            @Override
            public Iterator<T> iterator() {
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public T next() {
                        throw new NoSuchElementException();
                    }
                };
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            public String toString() {
                return "none";
            }
        }
    }

}