import random

def appraise_asset(prompt: str) -> dict:
    """
    根據提示詞鑑定資產屬性。
    生產級別可以調用 GPT-4o-mini 進行更精確的語義提取。
    """
    prompt_lower = prompt.lower()
    
    # 1. 基礎屬性 (1-100)
    # 如果提示詞包含 "ancient", "giant", "heavy" 等詞，增加強度和耐久
    power = random.randint(10, 60)
    durability = random.randint(20, 70)
    finesse = random.randint(5, 50)
    
    if "sword" in prompt_lower or "blade" in prompt_lower:
        power += 20
    if "stone" in prompt_lower or "metal" in prompt_lower:
        durability += 25
    if "gold" in prompt_lower or "elegant" in prompt_lower:
        finesse += 30

    # 2. 元素傾向
    elements = {
        "fire": ["fire", "flame", "lava", "hot", "red"],
        "water": ["water", "ice", "frost", "ocean", "blue"],
        "wind": ["wind", "storm", "cloud", "fly"],
        "light": ["holy", "god", "divine", "sun", "light"],
        "dark": ["dark", "shadow", "demon", "hell"]
    }
    
    affinity = "neutral"
    for element, keywords in elements.items():
        if any(k in prompt_lower for k in keywords):
            affinity = element
            break

    return {
        "power": min(100, power),
        "durability": min(100, durability),
        "finesse": min(100, finesse),
        "element": affinity,
        "rarity": "common" if power < 60 else ("epic" if power < 85 else "legendary")
    }
