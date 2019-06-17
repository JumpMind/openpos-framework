import { IGenericMaskSpec } from './textmask';
import createNumberMask from 'text-mask-addons/dist/createNumberMask';

/** Used by a GenericMaskSpec for building an array of mask elements to pass to text-mask.
 * Exists primarily for supporting server-side creation of masks.
 */
export interface IMaskElement {
    type: string;
    value: string;
}

export interface IMaskSpec {
    type: string;
}

export interface IGenericMaskSpec extends IMaskSpec {
    mask: IMaskElement[];
    guide: boolean;
    pipeName: string;
}

export interface INumberMaskSpec extends IMaskSpec {
    prefix: string;
    suffix: string;
    includeThousandsSeparator: boolean;
    allowDecimal: boolean;
    decimalSymbol: string;
    decimalLimit: number;
    requireDecimal: boolean;
    allowNegative: boolean;
    allowLeadingZeroes: boolean;
    integerLimit: number;
}

/**
 * Thin wrapper around text-mask's javascript interface
 */
export interface ITextMask {
    mask: ((string|RegExp)[] | (() => any)); // Array of strings/RegExs or function
    guide: boolean;
    pipe: Function;
}

/**
 * Builds an instance of the javascript object needed for text-mask
 */
export class TextMask implements ITextMask {
    public static NO_MASK: TextMask = new TextMask(() => false);

    mask: ((string|RegExp)[] | (() => any));
    guide: boolean;
    pipe: Function;

    constructor(mask?: (string|RegExp)[] | (() => any)) {
        this.mask = mask;
    }

    static instance(maskSpec: IMaskSpec): TextMask {
        const inst: TextMask = new TextMask();
        if (maskSpec.type === 'GenericMask') {
            const genericMaskSpec = <IGenericMaskSpec> maskSpec;
            inst.mask = [];
            for (const elem of genericMaskSpec.mask) {
                inst.mask.push(inst.toTextMaskElement(elem));
            }
            inst.guide = genericMaskSpec.guide;
            if (genericMaskSpec.pipeName) {
                inst.pipe = Pipes.instance.getPipe(genericMaskSpec.pipeName);
            }
        } else if (maskSpec.type === 'NumberMask') {
            const numMaskSpec = <INumberMaskSpec> maskSpec;
            inst.mask = createNumberMask(numMaskSpec);
        }
        return inst;
    }

    protected toTextMaskElement(elem: IMaskElement): string|RegExp {
        if (elem.type === 'String') {
            return elem.value;
        } else if (elem.type === 'RegExp') {
            return new RegExp(elem.value);
        }
    }
}

/**
 * A Class to provide access to text-mask Pipe functions which are implemented
 * per the specs documented here: https://github.com/text-mask/text-mask/blob/master/componentDocumentation.md#pipe
 */
class Pipes {
    private static _instance: Pipes;
    private pipeMap: Map<string, Function> = new Map<string, Function>();

    private toUpper: Function = (conformedValue, config) => {
        return conformedValue.toUpperCase();
    }

    private constructor() {
        this.pipeMap.set('ToUpper', this.toUpper);
        // Add new pipes here
    }

    public static get instance() {
        return this._instance || (this._instance = new this());
    }

    public getPipe(pipeName: string): Function {
        return this.pipeMap.get(pipeName);
    }

}
