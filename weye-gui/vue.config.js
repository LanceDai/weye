module.exports = {
    devServer: {
        disableHostCheck: true,
        hotOnly: true,
        host: 'localhost',
        port: 8080,
        proxy: {
            "/api": {
                target: "http://127.0.0.1:8081",
                changeOrigin: true,
                pathRewrite: {
                    "^/api": ""
                }
            }
        }
    }
}