# Command context example

This example project shows how to implement a custom CDI context with all the extras (including activation and deactivation).

## Mission

Suppose we have a simple functional interface:

```java
public interface Command {
    void execute();
}
```
Our mission is to provide a custom context so that it's possible to create bean instances whose lifecycle is bound to the command execution.
In other words, a `@CommandScoped` bean instance should be destroyed after the execution completes.
As a bonus we would like to be able to inject `CommandExecution` metadata (and see for example the time an execution started at).

## Implementation

First of all, we need to provide a scope annotation:

```java
@NormalScope
public @interface CommandScoped {
}
```

Note that the scope is _normal_.
This implies few facts and requirements:

* whenever you inject a `@CommandScoped` bean you get a client proxy, this allows e.g. to inject a `@CommandScoped` bean into `@ApplicationScoped` bean
* there may be no more than one mapped bean instance per `@CommandScoped` bean per thread

See also the spec - [6.3. Normal scopes and pseudo-scopes](http://docs.jboss.org/cdi/spec/1.2/cdi-spec.html#normal_scope).

The other important class is `CommandContextImpl`.
Few things to notice:

* `ThreadLocal` is used to store the map of bean instances
* there must be a way to activate/deactivate the context - see also `activate()` and `deactivate()` methods
* `InjectableCommandContext` is an injectable version of context which allow to detect the original "activator", so that it's possible to skip deactivation during `deactivate()`
* `ContextualInstance` wrapper allows to create and destroy a bean instance properly


## Activation

The custom context can be activated/deactivated either by `CommandDecorator`, `CommandExecutor` or manually - a `@Dependent` bean with bean type `CommandContext` and qualifier `@Default` is automatically registered.
See also the tests and compare the different ways of activating the context.