export class Time {
    constructor(public hours: number, public minutes: number, public seconds: number) {}
}

export class TimeUnitLabels {
    constructor(
        public hourLabel: string,
        public hourLabelPlural: string,
        public minuteLabel: string,
        public minuteLabelPlural: string,
        public secondLabel: string,
        public secondLabelPlural: string,
        public nowLabel: string){}
}

export function getHourMinSeconds( timeSeconds ) : Time {

    timeSeconds = Math.round(timeSeconds);
    const hour = Math.floor(timeSeconds / (60 * 60));
    const min = Math.floor((timeSeconds - (hour * 3600)) / 60);
    const sec = timeSeconds - (hour * 3600) - (min * 60 );

    return new Time(hour, min, sec);
}

export function getFormattedTime( timeFormat: string, time: Time, labels: TimeUnitLabels): string {
    let labelsToUse = {
        seconds: labels.secondLabel,
        minutes: labels.minuteLabel,
        hours: labels.hourLabel
    };

    if(time.seconds > 1) {
        labelsToUse.seconds = labels.secondLabelPlural;
    }

    if(time.minutes > 1) {
        labelsToUse.minutes = labels.minuteLabelPlural;
    }

    if(time.hours > 1) {
        labelsToUse.hours = labels.hourLabelPlural;
    }

    let formattedTime = timeFormat;

    let secondsValue = '';
    if(time.seconds > 0){
        secondsValue = `${time.seconds} ${labelsToUse.seconds}`;
    }
    formattedTime = formattedTime.replace('ss', secondsValue);

    let minutesValue = '';
    if(time.minutes > 0){
        minutesValue = `${time.minutes} ${labelsToUse.minutes}`;
    }
    formattedTime = formattedTime.replace('mm', minutesValue);

    let hoursValue = '';
    if(time.hours > 0){
        hoursValue = `${time.hours} ${labelsToUse.hours}`;
    }
    formattedTime = formattedTime.replace( 'HH', hoursValue);

    if(time.hours === 0 && time.minutes === 0 && time.seconds === 0){
        return labels.nowLabel;
    }

    return formattedTime.replace(/[ ]{2,}/g, ' ');
}