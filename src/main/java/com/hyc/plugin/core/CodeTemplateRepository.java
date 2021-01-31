package com.hyc.plugin.core;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
/**
 * @author hdr
 */
@State(name = "CodeTemplateRepository", storages = {@Storage("$APP_CONFIG$/CodeTemplateRepository.xml")})
public class CodeTemplateRepository implements ApplicationComponent, PersistentStateComponent<CodeTemplateRepository.State> {

    @NotNull
    @Override
    public String getComponentName() {
        return getClass().getSimpleName();
    }

    public State state = new State();

    public static class State {

        public State() {
        }

        public State(String foo) {
            this.foo = foo;
        }

        public String foo = "default";

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof State)) {
                return false;
            }

            State state = (State) o;

            return Objects.equals(foo, state.foo);
        }

        @Override
        public int hashCode() {
            return foo != null ? foo.hashCode() : 0;
        }
    }

    @Override
    @Nullable
    public CodeTemplateRepository.State getState() {
        System.err.println("----- getState -----");
        State newState = new State(Long.toString(System.currentTimeMillis()));
        this.state = newState;
        return newState;
    }

    @Override
    public void loadState(CodeTemplateRepository.@NotNull State state) {
        System.err.println("----- loadState -----");
        this.state = state;
    }

    public String getFoo() {
        if (this.state.foo == null) {
            this.state.foo = "https://jira.rz.is/rest/api/2/";
        }
        return this.state.foo;
    }

    public void setFoo(String foo) {
        this.state.foo = foo;
    }
}
