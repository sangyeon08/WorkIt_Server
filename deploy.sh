# ===== deploy.sh =====
#!/bin/bash

echo "WorkIt Docker 배포 시작"

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. 기존 컨테이너 중지 및 제거
echo -e "${YELLOW} 기존 컨테이너 중지 중...${NC}"
docker-compose down

# 2. 이미지 빌드
echo -e "${YELLOW} Docker 이미지 빌드 중...${NC}"
docker-compose build --no-cache

# 3. 컨테이너 실행
echo -e "${YELLOW}▶  컨테이너 실행 중...${NC}"
docker-compose up -d

# 4. 로그 확인
echo -e "${GREEN} 배포 완료!${NC}"
echo -e "${YELLOW} 로그 확인:${NC} docker-compose logs -f"
echo -e "${YELLOW} 접속:${NC} http://workit.digitalbasis.com:8087/swagger-ui/index.html"

# 5. 헬스 체크
echo -e "${YELLOW}🏥 헬스 체크 중...${NC}"
sleep 10

if curl -f http://localhost:8087/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN} 서버 정상 작동 중!${NC}"
else
    echo -e "${RED} 서버 시작 실패. 로그를 확인하세요.${NC}"
    docker-compose logs --tail=50
fi