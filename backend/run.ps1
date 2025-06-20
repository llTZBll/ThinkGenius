# 设置Java 21环境
Write-Host "Setting up Java 21 environment..." -ForegroundColor Green
$env:JAVA_HOME = "D:\Software\Java\jdk-21"
$env:PATH = "D:\Software\Java\jdk-21\bin;" + $env:PATH

# 验证Java版本
Write-Host "Java version:" -ForegroundColor Yellow
java -version

# 验证Maven版本
Write-Host "Maven version:" -ForegroundColor Yellow
mvn -version

# 清理项目
Write-Host "Cleaning project..." -ForegroundColor Green
mvn clean

# 编译项目
Write-Host "Compiling project..." -ForegroundColor Green
mvn compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    
    # 运行项目
    Write-Host "Starting Spring Boot application..." -ForegroundColor Green
    Write-Host "Health check: http://localhost:8081/health" -ForegroundColor Cyan
    Write-Host "Root endpoint: http://localhost:8081/" -ForegroundColor Cyan
    Write-Host "Simple test: http://localhost:8081/api/simple/ping" -ForegroundColor Cyan
    Write-Host "Test endpoint: http://localhost:8081/api/test/ping" -ForegroundColor Cyan
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
    
    # 运行Spring Boot应用
    mvn spring-boot:run
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
} 