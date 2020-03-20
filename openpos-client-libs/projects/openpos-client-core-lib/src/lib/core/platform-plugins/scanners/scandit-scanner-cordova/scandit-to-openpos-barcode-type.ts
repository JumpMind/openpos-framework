import {OpenposScanType} from '../openpos-scan-type.enum';
import {Barcode} from './types/barcode';

export class ScanditBarcodeUtils {

    private static scanditToOpenPosMap: Map<Barcode.Symbology, OpenposScanType> = new Map([
        [Barcode.Symbology.Aztec, OpenposScanType.AZTEK],
        [Barcode.Symbology.Codabar, OpenposScanType.CODABAR],
        [Barcode.Symbology.Code11, OpenposScanType.CODE11],
        [Barcode.Symbology.Code25, OpenposScanType.CODE25_NI2OF5],
        [Barcode.Symbology.Code39, OpenposScanType.CODE39],
        [Barcode.Symbology.Code93, OpenposScanType.CODE93],
        [Barcode.Symbology.Code128, OpenposScanType.CODE128],
        [Barcode.Symbology.EAN8, OpenposScanType.EAN8],
        [Barcode.Symbology.GS1Databar, OpenposScanType.GS1DATABAR],
        [Barcode.Symbology.MaxiCode, OpenposScanType.MAXICODE],
        [Barcode.Symbology.MSIPlessey, OpenposScanType.MSI_PLESSEY],
        [Barcode.Symbology.PDF417, OpenposScanType.PDF417],
        [Barcode.Symbology.QR, OpenposScanType.QRCODE],
        [Barcode.Symbology.RM4SCC, OpenposScanType.RM4SCC],
        [Barcode.Symbology.EAN13UPCA, OpenposScanType.UPCA],
        [Barcode.Symbology.UPCE, OpenposScanType.UPCE],
        [Barcode.Symbology.InterleavedTwoOfFive, OpenposScanType.ITF],
    ]);


    static convertToOpenposType( type: Barcode.Symbology ): OpenposScanType {
        if( this.scanditToOpenPosMap.has(type) ){
            return this.scanditToOpenPosMap.get(type);
        }

        throw Error(`Barcode type ${type} not supported`);
    }

    static convertFromOpenposType( type: OpenposScanType ): Barcode.Symbology {
        let matches  = Array.from( this.scanditToOpenPosMap.entries()).filter( value => value[1] === type );

        if(matches.length > 0) {
            return matches[0][0];
        }

        throw Error(`Barcode type ${type} not supported`);
    }
}
