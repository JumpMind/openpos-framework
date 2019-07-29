import { PersonalizationParameter } from './personalization-parameter.interface.';

export interface PersonalizationResponse {
    success?: boolean;
    message?: string;
    devicePattern?: string;
    parameters?: PersonalizationParameter[];
}
