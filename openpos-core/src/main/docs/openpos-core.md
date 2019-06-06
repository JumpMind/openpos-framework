---
id: openpos-core
title: OpenPOS Flow Framework
---

# Overview

Overall, openpos-core provides the infrastructure for defining the "flow" of a client-side retail application. It understands the steps and transitions though an application, as well as what screens to display in an abstract level.  It is **not aware** of a particular UI technology.  It says "display the sales screen with the following items" and allows the client to decide how to render that information as a screen on the UI. 

The openpos-core project contains much of what is considered the "controller" logic that resides in between the web front end client (the "view") and the business logic service code (the "model").  The core project provicdes   Specifically, core contains:

* The openpos state mahine. (The `org.jumpmind.pos.core.flow` package.)
* UIMessages such as HomeUIMessage, DialogUIMessage are abstracted screen types.
* Abstracted UI component models which make up the form screens (e.g. FormField, DateField, RadioField, etc., in the `org.jumpmind.pos.core.model` package)
* ScreenInterceptors are like filters which get run on each screen model before it is passed to the client.
* Screen templates (e.g. SellTemplate) are higher level templates for commonly used screen layouts.

# Key Classes
* **SessionSubscribedListener** - invoked by Spring when a new client connects over stomp.
* **StateManager** - dictates what the current screen of the application should be and handles actions as they come in.
* **ScreenService** - the screen service works with the MessageService to a) send screens to the client as JSON stompe messages and b) receive Actions from the client in the form of Json messages.
* **UIMessage** - represents a message (usually a screen) that is sent to the client.

# State Machine

![eclipse](assets/openpos-statemachine-highlevel.png)

The state machine is responsible for the "state" of a client application. 

## State machine concepts

### State: 
What state the application is in. (E.g. HomeScreenState) The state machine transitions from state to state.

![eclipse](assets/openpos-state.png)

A state can be represented by any java class and includes at least one method annotated with @OnArrive.

~~~~
public class HomeState {
    @OnArrive
    public void arrive(Action action) {
        showScreen();
    }
    ...
}
~~~~

States can also **inject** services, Spring beans or scoped context values.  

Services and Spring beans are typically injected using the `@Autowired` annotation.
~~~~
public class HomeState {

    @Autowired
    private IUserService userService;

    @OnArrive
    public void arrive(Action action) {
        showScreen();
    }
    ...
}
~~~~

### Scoped Context Values (@In, @InOut, and @Out)

Scoped context values represent values which need to be **shared between states, transition steps, and action handlers.**  The currently logged in user or current transaction are common exmamples of scoped context values.

Scoped values are injected into States using the @In annotation. You can also set scoped context values using the @Out annotation. The @InOut annotation combines the 2 concepts and will both attempt to inject an existing value and also "outject" the value back into the contet  after the state runs.

~~~
public class SaleState {

    @In(scope = ScopeType.Session, required=false)
    BusinessDate businessDate;

    @InOut(scope = ScopeType.Conversation, required=false)
    private RetailTransModel currentTransaction;

    @OnArrive
    public void arrive(Action action) {
        showScreen();
    }    

}    
~~~

#### @In annotation options:
* **name** - defaults to the name of the field. In the example above, the system will look for a value named businessDate and a value named currentTransaction.
* **autoCreate** - defaults to false.  If if's true and no context value is found for the given name, then try to create and a supply a new object that matches the target type. The newly created object will then be in the contect and available for injection into other states per the scope rules.
* **required** - defaults to true, it is a pre-condition of the state the named value must exist in the context prior to arriving at this state. For example all states that assume a transaciton and operate on the currentTransaction can leave this as the default of true and assume that there will be  a non-null currentTransaction when the state runs.
* **scope** - scope defines how long a particular value will live and is useful for making sure that stale values don't bleed between flows, transactions, etc.  Each context value must be assigned to a specific scope.

![eclipse](assets/openpos-scopes.png)

### Flow Yaml Config Files

States and their transitions are represented in "flow" yaml config files.  Consider this example"

~~~~
---
DefaultFlow:
  - HomeState: 
      Sell: SaleState
      Returns: ReturnState
  - SaleState:
      Back: HomeState
  - ReturnState:
      Back: HomeState      
~~~~

This flow config has the following semantics:
* The DefaultFlow will start on the HomeState because it's the first state list.
* On the HomeState, when the "Sell" action is encountered, transtion to the SaleState.  If the "Returns" action is encountered, transition to the "ReturnState".
* When on the SaleState, if the "Back" action is encountered, transition to the HomeState.
* When on the SalReturnStateState, if the "Back" action is encountered, transition to the HomeState.

### Action

**Actions**: these are events generated either by the client or by server side state logic.  (e.g. UsernameEntered).  Actions always have a name and can have an optional payload (data) which is usually a string or a Map structure.

### TransitionStep

Steps which execute during the transition from one state to another and can show screens and cancel the transition if necessary. Transition steps are good for cross-cutting needs such as ensuring a user is logged in or the device is open, for example.

![eclipse](assets/openpos-state-transition.png)

Transition steps are Spring beans which implement the ITransitionStep interface. Each TransitionStep has an **isApplication** method and **arrive** method which both receive a Transition object.  Transition steps can also use @Autowired and @In fields just like any state.

When a transition step is complete, it should call **transition.proceed()**.  If the transition needs to be canceled, then call **stransition.cancel()**.

~~~
@Component
@Order(150)
public class UserLoginStep implements ITransitionStep {

    @InOut(scope = ScopeType.Session, required = false)
    protected UserModel currentUser;

    @Override
    public boolean isApplicable(Transition transition) {
        return true;
    }

    @Override
    public void arrive(Transition transition) {
        promptForLogin();
    }
}
~~~

### Substate / Subflows

Normally, the state machine goes from state to state, with no concept of going "back" to a previous state.  All that is known (by default) is the current state and where that current state can transition to next. 

But sometimes you want to run a shared state or flow, such as customer lookup, and then **return** to the current state. That is what a subflow does: it allows for transition to a new state (or a series of states, as defined by a subflow yaml, for example) without exiting the current state.  When the subflow completes, control is returned to the state where the subflow was launched from (using a **ReturnAction**)






