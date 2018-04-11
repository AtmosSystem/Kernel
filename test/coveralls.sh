COVERALLS_URL='https://coveralls.io/api/v1/jobs'
lein cloverage -o cov --coveralls
echo $COVERALLS_REPO_TOKEN
echo 'klk'
curl -F 'json_file=@cov/coveralls.json' "$COVERALLS_URL"