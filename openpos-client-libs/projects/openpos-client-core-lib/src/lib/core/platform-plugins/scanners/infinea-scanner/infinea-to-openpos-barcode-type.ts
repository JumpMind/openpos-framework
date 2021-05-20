import { OpenposScanType } from '../openpos-scan-type.enum';
import { InfineaBarcodeType } from './infinea-barcode-type.enum';

export class InfineaBarcodeUtils {

    private static infineaToOpenposTypeMap = new Map([
        [InfineaBarcodeType.AZTEK, OpenposScanType.AZTEK],
        [InfineaBarcodeType.CCA, OpenposScanType.CCA],
        [InfineaBarcodeType.CCB, OpenposScanType.CCB],
        [InfineaBarcodeType.CCC, OpenposScanType.CCC],
        [InfineaBarcodeType.CODABAR, OpenposScanType.CODABAR],
        [InfineaBarcodeType.CODABAR_ABC, OpenposScanType.CODABAR_ABC],
        [InfineaBarcodeType.CODABAR_CX, OpenposScanType.CODABAR_CX],
        [InfineaBarcodeType.CODE11, OpenposScanType.CODE11],
        [InfineaBarcodeType.CODE128, OpenposScanType.CODE128],
        [InfineaBarcodeType.CODE25_I2OF5, OpenposScanType.CODE25_I2OF5],
        [InfineaBarcodeType.CODE25_NI2OF5, OpenposScanType.CODE25_NI2OF5],
        [InfineaBarcodeType.CODE39, OpenposScanType.CODE39],
        [InfineaBarcodeType.CODE39_FULL, OpenposScanType.CODE39_FULL],
        [InfineaBarcodeType.CODE93, OpenposScanType.CODE93],
        [InfineaBarcodeType.CPCBINARY, OpenposScanType.CPCBINARY],
        [InfineaBarcodeType.DATAMATRIX, OpenposScanType.DATAMATRIX],
        [InfineaBarcodeType.DUN14, OpenposScanType.DUN14],
        [InfineaBarcodeType.EAN128, OpenposScanType.EAN128],
        [InfineaBarcodeType.EAN13, OpenposScanType.EAN13],
        [InfineaBarcodeType.EAN13_2, OpenposScanType.EAN13_2],
        [InfineaBarcodeType.EAN13_5, OpenposScanType.EAN13_5],
        [InfineaBarcodeType.EAN2, OpenposScanType.EAN2],
        [InfineaBarcodeType.EAN5, OpenposScanType.EAN5],
        [InfineaBarcodeType.EAN8, OpenposScanType.EAN8],
        [InfineaBarcodeType.EAN8_2, OpenposScanType.EAN8_2],
        [InfineaBarcodeType.EAN8_5, OpenposScanType.EAN8_5],
        [InfineaBarcodeType.GS1DATABAR, OpenposScanType.GS1DATABAR],
        [InfineaBarcodeType.IATA, OpenposScanType.IATA],
        [InfineaBarcodeType.INTELLIGENT_MAIL, OpenposScanType.INTELLIGENT_MAIL],
        [InfineaBarcodeType.ITA_PHARMA, OpenposScanType.ITA_PHARMA],
        [InfineaBarcodeType.ITF14, OpenposScanType.ITF14],
        [InfineaBarcodeType.KOREAN_POSTAL, OpenposScanType.KOREAN_POSTAL],
        [InfineaBarcodeType.LATENT_IMAGE, OpenposScanType.LATENT_IMAGE],
        [InfineaBarcodeType.MATRIX_2OF5, OpenposScanType.MATRIX_2OF5],
        [InfineaBarcodeType.MAXICODE, OpenposScanType.MAXICODE],
        [InfineaBarcodeType.MICROPDF417, OpenposScanType.MICROPDF417],
        [InfineaBarcodeType.MSI_PLESSEY, OpenposScanType.MSI_PLESSEY],
        [InfineaBarcodeType.PDF417, OpenposScanType.PDF417],
        [InfineaBarcodeType.PHARMACODE, OpenposScanType.PHARMACODE],
        [InfineaBarcodeType.PLANET, OpenposScanType.PLANET],
        [InfineaBarcodeType.POSTBAR, OpenposScanType.POSTBAR],
        [InfineaBarcodeType.QRCODE, OpenposScanType.QRCODE],
        [InfineaBarcodeType.RM4SCC, OpenposScanType.RM4SCC],
        [InfineaBarcodeType.SCODE, OpenposScanType.SCODE],
        [InfineaBarcodeType.TELEPEN, OpenposScanType.TELEPEN],
        [InfineaBarcodeType.UK_PLESSEY, OpenposScanType.UK_PLESSEY],
        [InfineaBarcodeType.UPCA, OpenposScanType.UPCA],
        [InfineaBarcodeType.UPCA_2, OpenposScanType.UPCA_2],
        [InfineaBarcodeType.UPCA_5, OpenposScanType.UPCA_5],
        [InfineaBarcodeType.UPCE, OpenposScanType.UPCE],
        [InfineaBarcodeType.UPCE_2, OpenposScanType.UPCE_2],
        [InfineaBarcodeType.UPCE_5, OpenposScanType.UPCE_5]
    ]);

    static convertToOpenposType( type: InfineaBarcodeType ): OpenposScanType {
        if ( InfineaBarcodeUtils.infineaToOpenposTypeMap.has(type)) {
            return InfineaBarcodeUtils.infineaToOpenposTypeMap.get(type);
        }

        return null;
    }
}
