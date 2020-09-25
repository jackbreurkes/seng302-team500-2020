export default function log(message: String, level?: 'info' | 'warn' | 'error') {

    if (process.env.NODE_ENV !== 'production') {
        if (level === 'error') {
            console.error(message);
        } else if (level === 'warn') {
            console.warn(message);
        } else {
            console.log(message);
        }
    }

}