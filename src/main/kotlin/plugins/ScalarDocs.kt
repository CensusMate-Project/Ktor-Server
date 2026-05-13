package org.censusmate.plugins

fun scalarHtml(specUrl: String, title: String) = """
    <!doctype html>
    <html lang="ru">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>$title</title>
        <style>
            body { margin: 0; padding: 0; }
        </style>
    </head>
    <body>
        <script
            id="api-reference"
            data-url="$specUrl"
            data-configuration='{
                "theme": "purple",
                "layout": "modern",
                "defaultHttpClient": {
                    "targetKey": "shell",
                    "clientKey": "curl"
                }
            }'
        ></script>
        <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
    </body>
    </html>
""".trimIndent()