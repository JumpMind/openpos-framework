import { AilaBarcodeType } from './aila-barcode-type.enum';
import { OpenposScanType } from '../openpos-scan-type.enum';

export class AilaBarcodeUtils {

    static convertToOpenposType( type: AilaBarcodeType, length: number ): OpenposScanType {
        switch ( type ) {
            case AilaBarcodeType.UPC_EAN:
                // This is terrible but as of now we don't know the difference between EAN and UPC
                switch (length) {
                    case 8:
                        return OpenposScanType.UPCE;
                    case 13:
                        return OpenposScanType.UPCA;
                    default:
                        return null;
                }
            case AilaBarcodeType.QR:
                return OpenposScanType.QRCODE;
            case AilaBarcodeType.Code128:
                return OpenposScanType.CODE128;
            case AilaBarcodeType.Code25_2of5:
                // this is a guess
                return OpenposScanType.CODE25_I2OF5;
            case AilaBarcodeType.PDF417:
                return OpenposScanType.PDF417;
            case AilaBarcodeType.DataBar:
                return OpenposScanType.GS1DATABAR;
            default:
                return null;
        }
    }
}
