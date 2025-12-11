#!/usr/bin/env python3
"""
Gera QR Code para download do AgroColetor
"""
import qrcode

# URL do GitHub Releases
url = "https://github.com/SauloRodrigues20/Agrogeocolector/releases/latest"

# Criar QR Code
qr = qrcode.QRCode(
    version=1,
    error_correction=qrcode.constants.ERROR_CORRECT_L,
    box_size=10,
    border=4,
)

qr.add_data(url)
qr.make(fit=True)

# Gerar imagem
img = qr.make_image(fill_color="black", back_color="white")

# Salvar
output_path = "download_qrcode.png"
img.save(output_path)

print(f"✅ QR Code gerado: {output_path}")
print(f"📱 Aponta para: {url}")
print(f"\n📐 Tamanho: {img.size[0]}x{img.size[1]} pixels")
print(f"\n💡 Como usar:")
print(f"   1. Imprima este QR Code")
print(f"   2. Escaneie com a câmera do celular")
print(f"   3. Abre direto na página de download!")
