const { override, addLessLoader, babelInclude } = require("customize-cra");
const path = require("path");

module.exports = {
    webpack: override(
        addLessLoader({
            lessOptions: {
                javascriptEnabled: true,
            },
        }),
        babelInclude([path.resolve("src"), path.resolve("node_modules/metro4-react")])
    ),
};
