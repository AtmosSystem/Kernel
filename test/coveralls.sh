COVERALLS_URL='https://coveralls.io/api/v1/jobs?repo_token=$COVERALLS_REPO_TOKEN'
lein cloverage -o cov --coveralls
curl -F 'json_file=@cov/coveralls.json' "$COVERALLS_URL"