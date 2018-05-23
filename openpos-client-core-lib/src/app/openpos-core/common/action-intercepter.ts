
export class ActionIntercepter {

  private intercepter: FunctionActionIntercepter;
  private behavior: ActionIntercepterBehavior;

  constructor( intercepter: FunctionActionIntercepter, behavior: (ActionIntercepterBehavior|ActionIntercepterBehaviorType) ) {
    this.intercepter = intercepter;
    if (behavior instanceof ActionIntercepterBehavior) {
      this.behavior = behavior;
    } else {
      this.behavior = new ActionIntercepterBehavior(behavior, null);
    }
  }

  public intercept( payload: any,  continueWith: Function ) {
      switch (this.behavior.type) {
        case ActionIntercepterBehaviorType.before:
          console.log( 'Intercepting Action' );
          this.intercepter( payload );
          console.log( 'Continuing with default action handler');
          continueWith();
          break;

        case ActionIntercepterBehaviorType.block:
          console.log( 'Intercepting Action');
          this.intercepter( payload );
          if (this.behavior.optionalContinueEval) {
            if (this.behavior.optionalContinueEval(payload)) {
              console.log( 'continueEval returned true, continuing with default action handler');
              continueWith();
            } else {
              console.log( 'continueEval returned false, skipping default action handler');
            }
          } else {
            console.log( 'Skipping default action handler');
          }
          break;
      }
  }

}

export type FunctionActionIntercepter = (data?: any) => void;

export type FunctionContinueEval = (data?: any) => boolean;

export class ActionIntercepterBehavior {
  constructor(public type: ActionIntercepterBehaviorType, public optionalContinueEval?: FunctionContinueEval) {
  }
}

export enum ActionIntercepterBehaviorType {
  before,
  block
}
