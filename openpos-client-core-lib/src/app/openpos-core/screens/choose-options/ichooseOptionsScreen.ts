import { IOptionItem } from "../../self-checkout";

export interface IChooseOptionsScreen{
    options: IOptionItem[];
    selectionMode: SelectionMode;
    displayStyle: DisplayStyle;
    promptText: string;
}

export enum SelectionMode{
    None="None",
    Single="Single",
    Multiple="Multiple",
}

export enum DisplayStyle{
    ButtonList="ButtonList",
    ButtonGrid="ButonGrid"
}