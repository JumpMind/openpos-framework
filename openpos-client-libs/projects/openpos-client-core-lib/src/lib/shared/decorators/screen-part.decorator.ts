export interface ScreenPartProps {
    name: string;
}

export function ScreenPart( config: ScreenPartProps ) {
    return function<T extends {new(...args: any[]): {}}>(target: T) {
        const newClazz = class extends target {
            screenPartName = config.name;
            };
        return newClazz;
    };
}
