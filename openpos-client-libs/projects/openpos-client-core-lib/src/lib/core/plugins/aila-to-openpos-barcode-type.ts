import { AilaBarcodeType } from './aila-barcode-type.enum';
import { OpenposBarcodeType } from './openpos-barcode-type.enum';

export class AilaBarcodeUtils {

    static convertToOpenposType( type: AilaBarcodeType, length: number ): OpenposBarcodeType {
        switch ( type ) {
            case AilaBarcodeType.UPC_EAN:
                // This is terrible but as of now we don't know the difference between EAN and UPC
                switch (length) {
                    case 8:
                        return OpenposBarcodeType.UPCE;
                    case 13:
                        return OpenposBarcodeType.UPCA;
                    default:
                        return null;
                }
            case AilaBarcodeType.QR:
                return OpenposBarcodeType.QRCODE;
            case AilaBarcodeType.Code128:
                return OpenposBarcodeType.CODE128;
            case AilaBarcodeType.Code25_2of5:
                // this is a guess
                return OpenposBarcodeType.CODE25_I2OF5;
            case AilaBarcodeType.PDF417:
                return OpenposBarcodeType.PDF417;
            case AilaBarcodeType.DataBar:
                return OpenposBarcodeType.GS1DATABAR;
            default:
                return null;
        }
    }
}
