import { OpenposBarcodeType } from './openpos-barcode-type.enum';
import { InfineaBarcodeType } from './infinea-barcode-type.enum';

export class InfineaBarcodeUtils {

    private static infineaToOpenposTypeMap = new Map([
        [InfineaBarcodeType.AZTEK, OpenposBarcodeType.AZTEK],
        [InfineaBarcodeType.CCA, OpenposBarcodeType.CCA],
        [InfineaBarcodeType.CCB, OpenposBarcodeType.CCB],
        [InfineaBarcodeType.CCC, OpenposBarcodeType.CCC],
        [InfineaBarcodeType.CODABAR, OpenposBarcodeType.CODABAR],
        [InfineaBarcodeType.CODABAR_ABC, OpenposBarcodeType.CODABAR_ABC],
        [InfineaBarcodeType.CODABAR_CX, OpenposBarcodeType.CODABAR_CX],
        [InfineaBarcodeType.CODE11, OpenposBarcodeType.CODE11],
        [InfineaBarcodeType.CODE128, OpenposBarcodeType.CODE128],
        [InfineaBarcodeType.CODE25_I2OF5, OpenposBarcodeType.CODE25_I2OF5],
        [InfineaBarcodeType.CODE25_NI2OF5, OpenposBarcodeType.CODE25_NI2OF5],
        [InfineaBarcodeType.CODE39, OpenposBarcodeType.CODE39],
        [InfineaBarcodeType.CODE39_FULL, OpenposBarcodeType.CODE39_FULL],
        [InfineaBarcodeType.CODE93, OpenposBarcodeType.CODE93],
        [InfineaBarcodeType.CPCBINARY, OpenposBarcodeType.CPCBINARY],
        [InfineaBarcodeType.DATAMATRIX, OpenposBarcodeType.DATAMATRIX],
        [InfineaBarcodeType.DUN14, OpenposBarcodeType.DUN14],
        [InfineaBarcodeType.EAN128, OpenposBarcodeType.EAN128],
        [InfineaBarcodeType.EAN13, OpenposBarcodeType.EAN13],
        [InfineaBarcodeType.EAN13_2, OpenposBarcodeType.EAN13_2],
        [InfineaBarcodeType.EAN13_5, OpenposBarcodeType.EAN13_5],
        [InfineaBarcodeType.EAN2, OpenposBarcodeType.EAN2],
        [InfineaBarcodeType.EAN5, OpenposBarcodeType.EAN5],
        [InfineaBarcodeType.EAN8, OpenposBarcodeType.EAN8],
        [InfineaBarcodeType.EAN8_2, OpenposBarcodeType.EAN8_2],
        [InfineaBarcodeType.EAN8_5, OpenposBarcodeType.EAN8_5],
        [InfineaBarcodeType.GS1DATABAR, OpenposBarcodeType.GS1DATABAR],
        [InfineaBarcodeType.IATA, OpenposBarcodeType.IATA],
        [InfineaBarcodeType.INTELLIGENT_MAIL, OpenposBarcodeType.INTELLIGENT_MAIL],
        [InfineaBarcodeType.ITA_PHARMA, OpenposBarcodeType.ITA_PHARMA],
        [InfineaBarcodeType.ITF14, OpenposBarcodeType.ITF14],
        [InfineaBarcodeType.KOREAN_POSTAL, OpenposBarcodeType.KOREAN_POSTAL],
        [InfineaBarcodeType.LATENT_IMAGE, OpenposBarcodeType.LATENT_IMAGE],
        [InfineaBarcodeType.MATRIX_2OF5, OpenposBarcodeType.MATRIX_2OF5],
        [InfineaBarcodeType.MAXICODE, OpenposBarcodeType.MAXICODE],
        [InfineaBarcodeType.MICROPDF417, OpenposBarcodeType.MICROPDF417],
        [InfineaBarcodeType.MSI_PLESSEY, OpenposBarcodeType.MSI_PLESSEY],
        [InfineaBarcodeType.PDF417, OpenposBarcodeType.PDF417],
        [InfineaBarcodeType.PHARMACODE, OpenposBarcodeType.PHARMACODE],
        [InfineaBarcodeType.PLANET, OpenposBarcodeType.PLANET],
        [InfineaBarcodeType.POSTBAR, OpenposBarcodeType.POSTBAR],
        [InfineaBarcodeType.QRCODE, OpenposBarcodeType.QRCODE],
        [InfineaBarcodeType.RM4SCC, OpenposBarcodeType.RM4SCC],
        [InfineaBarcodeType.SCODE, OpenposBarcodeType.SCODE],
        [InfineaBarcodeType.TELEPEN, OpenposBarcodeType.TELEPEN],
        [InfineaBarcodeType.UK_PLESSEY, OpenposBarcodeType.UK_PLESSEY],
        [InfineaBarcodeType.UPCA, OpenposBarcodeType.UPCA],
        [InfineaBarcodeType.UPCA_2, OpenposBarcodeType.UPCA_2],
        [InfineaBarcodeType.UPCA_5, OpenposBarcodeType.UPCA_5],
        [InfineaBarcodeType.UPCE, OpenposBarcodeType.UPCE],
        [InfineaBarcodeType.UPCE_2, OpenposBarcodeType.UPCE_2],
        [InfineaBarcodeType.UPCE_5, OpenposBarcodeType.UPCE_5]
    ]);

    static convertToOpenposType( type: InfineaBarcodeType ): OpenposBarcodeType {
        if ( InfineaBarcodeUtils.infineaToOpenposTypeMap.has(type)) {
            return InfineaBarcodeUtils.infineaToOpenposTypeMap.get(type);
        }

        return null;
    }
}
