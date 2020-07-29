const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
      serverUrl:  'https://csse-s302g5.canterbury.ac.nz/sonarqube/',
      token: "c46163acc7d07e435e7438510d4fcae1c9b1253d",
      options : {
        'sonar.projectKey': 'team-500-client',
        'sonar.projectName': 'Team 500 - Client',
        "sonar.sourceEncoding": "UTF-8",
        'sonar.sources': 'src',
        'sonar.tests': 'src',
        'sonar.inclusions': '**',
        'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts',
        'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
        'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
        'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
      }
    }, () => {});