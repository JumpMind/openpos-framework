export interface IMessageDialogProperties {
    title?: string;
    mainMessages: string[];
    detailMessages?: string[];
    detailsTitle?: string;
    scrollable?: boolean;
    confirmButtonName?: string;
    cancelButtonName?: string;
}
