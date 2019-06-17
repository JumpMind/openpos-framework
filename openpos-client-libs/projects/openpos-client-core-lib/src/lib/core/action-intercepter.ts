import { Logger } from './services/logger.service';


export class ActionIntercepter {

  private intercepter: FunctionActionIntercepter;
  private behavior: ActionIntercepterBehavior;

  constructor(private log: Logger, intercepter: FunctionActionIntercepter, behavior: (ActionIntercepterBehavior|ActionIntercepterBehaviorType),
        public options?: ActionIntercepterOptions ) {
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
          this.log.info( 'Intercepting Action' );
          this.intercepter( payload );
          this.log.info( 'Continuing with default action handler');
          continueWith();
          break;

        case ActionIntercepterBehaviorType.block:
          this.log.info( 'Intercepting Action');
          this.intercepter( payload );
          if (this.behavior.optionalContinueEval) {
            if (this.behavior.optionalContinueEval(payload)) {
              this.log.info( 'continueEval returned true, continuing with default action handler');
              continueWith();
            } else {
              this.log.info( 'continueEval returned false, skipping default action handler');
            }
          } else {
            this.log.info( 'Skipping default action handler');
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

export interface ActionIntercepterOptions {
    showLoadingAfterIntercept?: boolean;
}
