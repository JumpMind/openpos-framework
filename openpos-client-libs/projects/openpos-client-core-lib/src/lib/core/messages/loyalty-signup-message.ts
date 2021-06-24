import { MessageTypes } from './message-types';
import { OpenposMessage } from './message';

export class LoyaltySignupMessage implements OpenposMessage {
    type = MessageTypes.LOYALTY_SIGNUP;
    isActiveOnCustomerDisplay: boolean;
    message: string;
}
