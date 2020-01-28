import { PersonalizationParameter } from './personalization-parameter.interface.';

export interface PersonalizationConfigResponse {
    devicePattern?: string;
    parameters?: PersonalizationParameter[];

    //If this is from the ManagementServer we will also get these properties
    openposManagementServer?: boolean;
}
