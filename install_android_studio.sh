#!/usr/bin/env bash
# ==============================================================================
# SamVideo - Script de Instalação Automatizada do Android Studio
# Autor: Sam Moreno
# Licença: BSD 2-Clause
# ==============================================================================

set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${CYAN}====================================================${NC}"
echo -e "${CYAN}   SamVideo - Instalador do Android Studio          ${NC}"
echo -e "${CYAN}   Autor: Sam Moreno | Licença: BSD 2-Clause        ${NC}"
echo -e "${CYAN}====================================================${NC}\n"

# 1. Verifica se está rodando em ambiente Linux
if [[ "$OSTYPE" != "linux-gnu"* ]]; then
    echo -e "${YELLOW}[AVISO] Este script foi desenvolvido principalmente para Linux (Ubuntu/Debian/Fedora/Arch).${NC}"
    echo -e "No macOS ou Windows, baixe o instalador oficial diretamente em: https://developer.android.com/studio"
    exit 1
fi

echo -e "${GREEN}[1/4] Verificando dependências de sistema (Java 17 e bibliotecas)...${NC}"

# Detecta gerenciador de pacotes
if command -v apt-get &> /dev/null; then
    echo "Gerenciador APT detectado (Ubuntu/Debian)..."
    sudo apt-get update -y
    sudo apt-get install -y openjdk-17-jdk curl wget unzip libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1 libbz2-1.0:i386 2>/dev/null || \
    sudo apt-get install -y openjdk-17-jdk curl wget unzip
elif command -v dnf &> /dev/null; then
    echo "Gerenciador DNF detectado (Fedora/RHEL)..."
    sudo dnf install -y java-17-openjdk-devel curl wget unzip
elif command -v pacman &> /dev/null; then
    echo "Gerenciador Pacman detectado (Arch Linux)..."
    sudo pacman -Sy --noconfirm jdk17-openjdk curl wget unzip
else
    echo -e "${YELLOW}[AVISO] Gerenciador de pacotes não identificado. Certifique-se de ter o JDK 17 e curl instalados.${NC}"
fi

echo -e "\n${GREEN}[2/4] Verificando método de instalação recomendado...${NC}"

# Opção A: Snap (Se disponível, é o método mais limpo e auto-atualizável)
if command -v snap &> /dev/null; then
    echo -e "${CYAN}Snap detectado! Instalando Android Studio via Snap (canal stable)...${NC}"
    sudo snap install android-studio --classic
    echo -e "${GREEN}✓ Android Studio instalado com sucesso via Snap!${NC}"
    echo -e "Você pode executá-lo a qualquer momento digitando: ${CYAN}android-studio${NC}"
    exit 0
fi

# Opção B: Download e Extração direta do tarball oficial em /opt
STUDIO_DIR="/opt/android-studio"
INSTALL_URL="https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2024.2.1.12/android-studio-2024.2.1.12-linux.tar.gz"
TEMP_TAR="/tmp/android-studio.tar.gz"

echo -e "\n${GREEN}[3/4] Baixando a versão oficial mais recente do Android Studio...${NC}"
wget -O "$TEMP_TAR" "$INSTALL_URL" --show-progress

echo -e "\n${GREEN}[4/4] Instalando em $STUDIO_DIR...${NC}"
sudo rm -rf "$STUDIO_DIR"
sudo tar -xzf "$TEMP_TAR" -C /opt/
rm -f "$TEMP_TAR"

# Cria atalho no PATH
sudo ln -sf "$STUDIO_DIR/bin/studio.sh" /usr/local/bin/android-studio

# Cria lançador .desktop para o menu de aplicativos
DESKTOP_FILE="$HOME/.local/share/applications/android-studio.desktop"
mkdir -p "$HOME/.local/share/applications"
cat << EOF > "$DESKTOP_FILE"
[Desktop Entry]
Version=1.0
Type=Application
Name=Android Studio
Comment=The Official Android IDE
Exec=/opt/android-studio/bin/studio.sh %f
Icon=/opt/android-studio/bin/studio.png
Categories=Development;IDE;
Terminal=false
StartupWMClass=jetbrains-studio
EOF

chmod +x "$DESKTOP_FILE"

echo -e "\n${CYAN}====================================================${NC}"
echo -e "${GREEN}✓ Instalação concluída com sucesso!${NC}"
echo -e "Para iniciar o Android Studio:"
echo -e "  1. Digite no terminal: ${CYAN}android-studio${NC}"
echo -e "  2. Ou abra pelo menu do seu sistema: 'Android Studio'"
echo -e "  3. No menu inicial do Android Studio, escolha 'Open' e selecione a pasta do ${CYAN}SamVideo${NC}."
echo -e "${CYAN}====================================================${NC}"
