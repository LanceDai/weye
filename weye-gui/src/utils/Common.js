export function outputObj(obj) {
    for (const i in obj) {
        console.log(i + " = " + obj[i] + "\n");
    }
}

export function isEmpty(obj) {
    return obj === undefined || obj === null || obj === '' || obj === ' '
}