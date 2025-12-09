/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Golem extends JLabel {
    private GameFrame parentFrame;
    private GameMap map;
    private Random rand = new Random();
    
    //----- Golem Stats
    private int width = GameConstants.GOLEM_WIDTH;
    private int height = GameConstants.GOLEM_HEIGHT;
    private int curX;
    private int curY;
    private int hp = GameConstants.GOLEM_MAX_HP;
    private int vx = 0;
    private int vy = 0;
    private int gravity = GameConstants.GRAVITY;
    private boolean onGround = true;
    private boolean turnLeft = true;
    private boolean lockedDirection = true;
    private boolean isDead = false;
    private long deathTimeEnd = 0;
    protected long hitTime = 0;
    private long flashTime = 0;
    private final long FLASH_DURATION = 100;
    private int currentAnimFrame = 0;

    //----- Attack Constants and State
     private final int MAX_JUMP_SPEED_X = 30;
    private final int SLIDE_SPEED = 15;
    private final int JUMP_STRENGTH = -25;
    private final long PRESLIDE_CHARGE_TIME = 800;
    private final int GROUNDHIT_ROCK_VX = 4; // ความเร็วแนวนอนสำหรับ Rock ที่พุ่งเฉียง
    
    //----- Phase and Cooldown Constants
    private final int HP_PHASE_2 = GameConstants.GOLEM_MAX_HP / 2;
    private final long RECHARGE_DURATION_PHASE1 = GameConstants.GOLEM_RECHARGE_TIME;
    private final long RECHARGE_DURATION_PHASE2 = (long)(GameConstants.GOLEM_RECHARGE_TIME * 0.8);
    
    // เพิ่มตัวแปรสำหรับเช็คท่าซ้ำ
private int lastMove = -1;           // เก็บท่าล่าสุดที่ใช้ (0, 1, หรือ 2)
private int consecutiveMoveCount = 0; // นับจำนวนครั้งที่ใช้ท่านั้นซ้ำ
    
    // ----- Sound Effects -----
    private GameSoundEffect attackSFX;
    private GameSoundEffect jumpSFX;
    private GameSoundEffect landSFX;
    private GameSoundEffect slideSFX;
    private GameSoundEffect hitSFX;
    private GameSoundEffect deadSFX;
    
    private enum GolemState {
        IDLE, ATTACK1, PREJUMP, ONAIR, FALL, GROUNDHIT, PRESLIDE, SLIDE, SLIDE_END, RECHARGE
    }
    private GolemState currentState = GolemState.IDLE;
    private long lastActionTime = 0;
    
    //----- Attack Animation Labels/Icons
    private JLabel attackLabel; // ใช้สำหรับท่าโจมตีที่ขยายขนาด
    private JLabel particleLabel; // สำหรับ GroundHit particle
    private JLabel particleLabel2;
    private JLabel jumpLabel;
    private int atk_width = GameConstants.GOLEM_ATK_WIDTH;
    private int atk_height = GameConstants.GOLEM_ATK_HEIGHT;
    private int jump_width = GameConstants.GOLEM_JUMP_WIDTH;
    private int jump_height = GameConstants.GOLEM_JUMP_HEIGHT;
    
    private GameIcon idleGif, deadGif, hitGif;
    private GameIcon idleFlip, deadFlip, hitFlip;
    
    // Move 1
    private GameIcon attack1Gif, attack1Flip;
    
    // Move 2
    private GameIcon preJumpGif, onAirGif, fallGif, groundHitGif, particleGif;
    private GameIcon preJumpFlip, onAirFlip, fallFlip, groundHitFlip, particleFlip, particleSlide, particleSlideFlip;
    
    // Move 3
    private GameIcon slideGif, slideFlip;
    
    public Golem(GameFrame pf, GameMap m, int startX, int startY, String gameDifficulty) {
        parentFrame = pf;
        this.map  = m;
        this.curX = startX;
        this.curY = startY;
        
        setDifficultyStats(gameDifficulty);
        
        // Load Golem Sprites (assuming Flip versions exist/can be flipped)
        idleGif = new GameIcon(GameConstants.FILE_GOLEM_IDLE).resize(width, height);
        deadGif = new GameIcon(GameConstants.FILE_GOLEM_DEAD).resize(width, height);
        hitGif  = new GameIcon(GameConstants.FILE_GOLEM_HIT_SPRITE).resize(width, height);

        
        // Move 1: Attack1
        attack1Gif  = new GameIcon(GameConstants.FILE_GOLEM_ATTACK1).resize(atk_width, atk_height);
        attack1Flip = new GameIcon(GameConstants.FILE_GOLEM_ATTACK1FLIP).resize(atk_width, atk_height);
        
        // Move 2: Jump Slam
        preJumpGif  = new GameIcon(GameConstants.FILE_GOLEM_PREJUMP).resize(jump_width, jump_height);
        onAirGif    = new GameIcon(GameConstants.FILE_GOLEM_ONAIR).resize(jump_width, jump_height);
        fallGif     = new GameIcon(GameConstants.FILE_GOLEM_FALL).resize(jump_width, jump_height);
        groundHitGif = new GameIcon(GameConstants.FILE_GOLEM_GROUNDHIT).resize(width, height);
        particleGif  = new GameIcon(GameConstants.FILE_GOLEM_PARTICLE).resize(atk_width, atk_height / 2);
        
        // Move 3: Slide
        slideGif    = new GameIcon(GameConstants.FILE_GOLEM_SLIDE).resize(width, height);
        particleSlide = new GameIcon(GameConstants.FILE_GOLEM_SLIDEPARTICLE).resize(width, height);
        particleSlideFlip = new GameIcon(GameConstants.FILE_GOLEM_SLIDEPARTICLEFLIP).resize(width, height);
        
        // Sound setup
        attackSFX = new GameSoundEffect(GameConstants.FILE_GOLEM_ATTACK_SFX);
        jumpSFX   = new GameSoundEffect(GameConstants.FILE_GOLEM_JUMP_SFX);
        landSFX   = new GameSoundEffect(GameConstants.FILE_GOLEM_LAND_SFX);
        slideSFX  = new GameSoundEffect(GameConstants.FILE_GOLEM_SLIDE_SFX);
        hitSFX    = new GameSoundEffect(GameConstants.FILE_GOLEM_HIT_SFX);
        deadSFX   = new GameSoundEffect(GameConstants.FILE_GOLEM_DEAD_SFX);
        
        // Setup auxiliary labels
        attackLabel = new JLabel();
        attackLabel.setVisible(false);
        //attackLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2)); // debug
        
        jumpLabel = new JLabel();
        jumpLabel.setVisible(false);
        //jumpLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // debug
        
        particleLabel = new JLabel();
        particleLabel.setVisible(false);
        //particleLabel.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2)); // debug
        
        particleLabel2 = new JLabel(); // เพิ่มบรรทัดนี้
        particleLabel2.setVisible(false);
        //particleLabel2.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2)); // debug
        
        setBounds(curX, curY, width, height);
        setIcon(idleGif);
        //setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // debug
        
        parentFrame.getDrawPane().add(this);
        parentFrame.getDrawPane().add(attackLabel);
        parentFrame.getDrawPane().add(particleLabel);
        parentFrame.getDrawPane().add(particleLabel2);
        parentFrame.getDrawPane().add(jumpLabel);
    }
    
    public Rectangle getHitBox() {
        return new Rectangle(curX, curY, width, height);
    }

    // Hitbox สำหรับการโจมตีระยะประชิด (Move 1)
    public Rectangle getAttack1HitBox() {
        if (turnLeft) {
            return new Rectangle(curX - (atk_width - width), curY, atk_width, height);
        } else {
            return new Rectangle(curX, curY, atk_width, height);
        }
    }

    // Hitbox สำหรับ GroundHit Particle (Move 2)
    public Rectangle getParticleHitBox() {
        if (particleLabel.isVisible()) {
             // กำหนดพื้นที่ดาเมจจากแรงสั่นสะเทือนให้กว้างกว่าตัว Golem
             return new Rectangle(curX - 50, curY + height - 10, width + 100, atk_height / 2); 
        }
        return new Rectangle(0, 0, 0, 0);
    }
    
    public void setDifficultyStats(String difficulty) {
        int baseHp = hp;

        if ("Easy".equals(difficulty)) {
            hp = (int)(baseHp * 0.7);
        } else if ("Normal".equals(difficulty)) {
            hp = baseHp;
        } else if ("Hard".equals(difficulty)) {
            hp = (int)(baseHp * 1.5);
        } else if ("Expert".equals(difficulty)) {
            hp = (int)(baseHp * 2.0);
        } else if ("Nightmare".equals(difficulty)) {
            hp = (int)(baseHp * 3.0);
        }
    }
    
    public JLabel getAttackLabel() { return attackLabel; }
    public JLabel getParticleLabel() { return particleLabel; }
    public JLabel getParticleLabel2() { return particleLabel2; }
    public JLabel getJumpLabel() { return jumpLabel; }
    public int getHp() { return hp; }
    public boolean isDead() { return isDead; }

    // Logic การรับดาเมจ
    public void damage(int d) {
        if (isDead) return;
         if (System.currentTimeMillis() < hitTime){
            return;
        }
        hp -= d;
        System.out.printf("Golem HP : %d\n", hp);
        if (hp <= 0) {
            hp = 0;
            isDead = true;
            if (deadSFX != null) deadSFX.playOnce();
            setDead();
            System.out.printf("Golem Dead\n");
        } else {
            if (hitSFX != null) hitSFX.playOnce();
            hitTime = System.currentTimeMillis() + GameConstants.DURATION_MS;
            flashTime = System.currentTimeMillis() + FLASH_DURATION;
        }
    }
    
    public void updateLocation() {
        if (isDead) {
            vx = 0;
            vy = 0;
            setDead();
            setLocation(curX, curY);
            return;
        }
        
        // Gravity
        vy += gravity;

        int nextY = curY + vy;
        int checkY_top = curY + 5;
        
        // Collision Up (Head hit)
        if (vy < 0) {
            checkY_top = nextY;
            boolean headLeftHit = map.isSolid(curX + 10, checkY_top); 
            boolean headRightHit = map.isSolid(curX + width - 10, checkY_top); 

            if (headLeftHit || headRightHit) {
                vy = 0;
                int tileRow = checkY_top / map.TILE_SIZE;
                curY = (tileRow + 1) * map.TILE_SIZE;
                nextY = curY;
            }
        }

        // Collision Down (Foot hit)
        boolean leftFoot  = map.isSolid(curX + 10, nextY + height);  
        boolean rightFoot = map.isSolid(curX + width - 10, nextY + height);
        
        boolean isKnockedBack = System.currentTimeMillis() < hitTime;

        if (vy >= 0 && (leftFoot || rightFoot)) {   // ถ้ามีความเร็วลง และ เท้าแตะพื้น
            if (!onGround) {
                // ถ้าเพิ่งลงพื้น
                if (currentState == GolemState.FALL) {
                    if (landSFX != null) landSFX.playOnce();
                    currentState = GolemState.GROUNDHIT;
                    lastActionTime = System.currentTimeMillis();
                    currentAnimFrame = 0;
                } else if (currentState != GolemState.GROUNDHIT) {
                    currentState = GolemState.IDLE;
                }
            }
            vy = 0;
            onGround = true;

            int tileRow = (nextY + height) / map.TILE_SIZE;
            curY = (tileRow * map.TILE_SIZE) - height;
        }  
        else { // ถ้ามีความเร็วขึ้น
            curY = nextY;
            onGround = false;
            if (currentState == GolemState.PREJUMP) {
                currentState = GolemState.ONAIR;
            } else if (currentState == GolemState.ONAIR && vy > 0) {
                currentState = GolemState.FALL;
            }
        }
        
        // X movement and side collision (simplified for Boss)
        curX += vx;
        
        // Clamp X to screen bounds
        if (curX < 0) curX = 0;
        if (curX > GameConstants.FRAME_WIDTH - width) curX = GameConstants.FRAME_WIDTH - width;

        setLocation(curX, curY);
        updateVisuals();
    }
    
    public void updateBehavior(Knight player) {  
        if (isDead) {
            setDead();
            return; 
        }
        
        int playerCenterX = player.getCurX() + player.getWidth() / 2;
        int monsterCenterX = curX + width / 2;
        int distanceX = playerCenterX - monsterCenterX;
        int absDistanceX = Math.abs(distanceX);
        
        // phase detect
        boolean isPhase2 = hp <= HP_PHASE_2;
        long currentRechargeDuration = isPhase2 ? RECHARGE_DURATION_PHASE2 : RECHARGE_DURATION_PHASE1;
        
        // ตั้งค่าทิศทาง
        if (currentState != GolemState.ONAIR && currentState != GolemState.FALL) {
        turnLeft = distanceX < 0;
        }

        // Boss State Machine
        switch (currentState) {
            case IDLE:
                vx = 0; 
                    int chosenMove;
                    
                    // --- 1. สุ่มท่าตามปกติ (Logic เดิม) ---
                    if (isPhase2) {
                        // Phase 2: (0=Slide, 1=Jump, 2=Slide)
                        chosenMove = rand.nextInt(3); 
                        if (chosenMove == 0 || chosenMove == 2) chosenMove = 2; // Slide
                        else chosenMove = 1; // Jump
                    } else {
                        // Phase 1: สุ่ม 3 ท่า (0=Attack1, 1=Jump, 2=Slide)
                        chosenMove = rand.nextInt(3); 
                    }

                    // --- 2. ตรวจสอบว่าใช้ท่าเดิมซ้ำเกิน 2 รอบหรือไม่ ---
                    if (chosenMove == lastMove && consecutiveMoveCount >= 2) {
                        // ถ้าซ้ำ ให้บังคับเปลี่ยนท่า (Force Change)
                        if (isPhase2) {
                            // Phase 2 มีแค่ท่า 1 กับ 2 ถ้าท่าปัจจุบันติดล็อค ให้สลับเป็นอีกท่าทันที
                            if (chosenMove == 2) chosenMove = 1; // ถ้า Slide ซ้ำ -> เปลี่ยนเป็น Jump
                            else chosenMove = 2; // ถ้า Jump ซ้ำ -> เปลี่ยนเป็น Slide
                        } else {
                            // Phase 1 มีท่า 0, 1, 2 ให้ขยับไปท่าถัดไป (เช่น 0->1, 1->2, 2->0)
                            chosenMove = (chosenMove + 1) % 3;
                        }
                    }

                    // --- 3. อัปเดตประวัติการใช้ท่า ---
                    if (chosenMove == lastMove) {
                        consecutiveMoveCount++;
                    } else {
                        lastMove = chosenMove;
                        consecutiveMoveCount = 1;
                    }
                    
                    if (chosenMove == 0 && !isPhase2) { // Attack 1 เฉพาะ Phase 1
                        currentState = GolemState.ATTACK1;
                        if (attackSFX != null) attackSFX.playOnce();
                    } else if (chosenMove == 1 && onGround) { // Jump Slam
                        currentState = GolemState.PREJUMP;
                        if (jumpSFX != null) jumpSFX.playOnce();
                    } else { // Slide (หรือเป็นท่า default ของ Phase 2)
                        currentState = GolemState.PRESLIDE; 
                    }
                    lastActionTime = System.currentTimeMillis(); // Reset timer for new action
                break;
                
            case ATTACK1:
                vx = 0;
                if (System.currentTimeMillis() > lastActionTime+920) { // ระยะเวลา Animation
                    currentState = GolemState.RECHARGE;
                    lastActionTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() > lastActionTime + 900 && currentAnimFrame != 99) { // สมมติว่าเฟรมที่ 400ms เป็นเฟรมที่ "ง้างหมัด"
                    // *** Rock Projectile Attack Logic ***
                    // ตรวจสอบว่ายังไม่ได้เสกหินในรอบนี้ (ใช้ currentAnimFrame เป็น flag ชั่วคราว)
                    if (currentAnimFrame == 0 || currentAnimFrame == 99) {
                        vx = 0;
                        int rockX, rockY;
                        int rockVX = (turnLeft ? -GameConstants.ROCK_PROJECTILE_VX : GameConstants.ROCK_PROJECTILE_VX);

                        // ตำแหน่ง Rock ออกจากหมัด (ปรับ offset ให้ดูสมจริง)
                        rockY = curY + 20; // ตำแหน่งกลางๆ ของตัว Golem
                        if (turnLeft) {
                            rockX = curX;
                        } else {
                            rockX = curX + width - 50;
                        }

                        // Spawn Rock พร้อมความเร็วเริ่มต้น vy=0 (ยิงตรง)
                        parentFrame.spawnRock(rockX, rockY, rockVX, 0);

                        currentAnimFrame = 99; // ตั้ง Flag ว่าได้เสกหินแล้ว
                    }
                } else if (System.currentTimeMillis() <= lastActionTime + 300) {
                    vx = turnLeft ? 5 : -5;
                    currentAnimFrame = 0; // Reset Flag เมื่อเริ่ม Animation
                }
                break;

            case PREJUMP: // Move 2: เตรียมกระโดด (Charge up)
                vx = 0;
                if (System.currentTimeMillis() > lastActionTime + 900) { // 0.3s Charge
                      // --- Aiming Logic Start: Calculate vx to land near the player ---
                    
                    // 1. Calculate time of flight (t = -2 * v_y0 / a) in game ticks
                    final double flightTime = (double) (-2.0 * JUMP_STRENGTH) / gravity; 
                    
                    // 2. Calculate required vx
                    if (flightTime > 0) {
                        // dx / t_flight, rounded to nearest integer
                        vx = (int) Math.round(distanceX / flightTime); 
                    } else {
                        vx = 0;
                    }

                    // 3. Cap speed (prevent unrealistically fast movement)
                    if (vx > MAX_JUMP_SPEED_X) vx = MAX_JUMP_SPEED_X;
                    if (vx < -MAX_JUMP_SPEED_X) vx = -MAX_JUMP_SPEED_X;
                    
                    // --- Aiming Logic End ---

                    lockedDirection = distanceX < 0; // Lock direction based on final target
                    vy = JUMP_STRENGTH;
                    lastActionTime = System.currentTimeMillis();
                    // State จะเปลี่ยนไป ONAIR ใน updateLocation เมื่อหลุดจากพื้นน
                }
                break;

            case ONAIR: // Move 2: ลอยกลางอากาศ
            case FALL: // Move 2: ตก
                break;

            case GROUNDHIT: // Move 2: กระแทกพื้น (Particle damage, Rock spawn)
                vx = 0;
                final int FRAME_WIDTH = GameConstants.FRAME_WIDTH;
                final int FRAME_HEIGHT = GameConstants.FRAME_HEIGHT;
                final int ROCK_WIDTH = GameConstants.ROCK_WIDTH;
       
                 // Rock 1
                if (currentAnimFrame == 0 && System.currentTimeMillis() > lastActionTime + 100) { 
                    int rockX1 = (int)(FRAME_WIDTH * 0.80) - (ROCK_WIDTH / 2);  
                    int rockY1 = 0 - ROCK_WIDTH; 
                    parentFrame.spawnRock(rockX1, rockY1, -GROUNDHIT_ROCK_VX * 3, GameConstants.ROCK_FALL_VY * 5);
                    
                    currentAnimFrame = 1; // Mark Rock 1 as spawned
                }
                
                // Rock 2
                if (currentAnimFrame == 1 && System.currentTimeMillis() > lastActionTime + 370) {
                     int rockX2 = (int)(FRAME_WIDTH); 
                    int rockY2 = (int)(FRAME_HEIGHT * 0.20); 

                    // Velocity: VX ต่ำ (พุ่งเฉียงซ้ายช้า), VY * 3 (ร่วงเร็ว)
                    parentFrame.spawnRock(rockX2, rockY2, -GROUNDHIT_ROCK_VX * 3, GameConstants.ROCK_FALL_VY * 5);
                    
                    currentAnimFrame = 99; // Mark all rocks as spawned
                }
                
                if (System.currentTimeMillis() > lastActionTime + 500) { // ระยะเวลา Particle
                    currentState = GolemState.RECHARGE;
                    lastActionTime = System.currentTimeMillis();
                }
                break;
                
            case PRESLIDE:
                vx = 0; // ต้องหยุดนิ่งเพื่อเตือนผู้เล่น
                // Lock ทิศทางการพุ่งเมื่อชาร์จครบ
                if (System.currentTimeMillis() > lastActionTime + PRESLIDE_CHARGE_TIME) { 
                    lockedDirection = turnLeft;
                    currentState = GolemState.SLIDE;
                    if (slideSFX != null) slideSFX.playOnce();
                    lastActionTime = System.currentTimeMillis();
                    currentAnimFrame = 0;
                }
                break;
                
            case SLIDE: // Move 3: พุ่ง
                // พุ่งเร็วไปทางผู้เล่น
                vx = lockedDirection ? -SLIDE_SPEED : SLIDE_SPEED;
                if (System.currentTimeMillis() > lastActionTime + 800) { // 0.8s Slide Duration
                    currentState = GolemState.SLIDE_END;
                    if (slideSFX != null) slideSFX.stop();
                    lastActionTime = System.currentTimeMillis();
                }
                break;
                
            case SLIDE_END: // Move 3: หยุดพุ่ง
                vx = 0;
                if (System.currentTimeMillis() > lastActionTime + 100) { // หยุดชั่วครู่
                     if (currentAnimFrame == 0) {
                        int rockX1 = (int)(GameConstants.FRAME_WIDTH * 0.25) - (GameConstants.ROCK_WIDTH / 2);
                        int rockX2 = (int)(GameConstants.FRAME_WIDTH * 0.50) - (GameConstants.ROCK_WIDTH / 2);
                        int rockX3 = (int)(GameConstants.FRAME_WIDTH * 0.75) - (GameConstants.ROCK_WIDTH / 2);
                        int rockY = 0 - GameConstants.ROCK_HEIGHT; // Rock จะเริ่มจากขอบบนสุด
                        
                        // Rock 1: 25% (ร่วงตรง)
                        parentFrame.spawnRock(rockX1, rockY, 0, GameConstants.ROCK_FALL_VY);
                        // Rock 2: 50% (ร่วงตรง)
                        parentFrame.spawnRock(rockX2, rockY, 0, GameConstants.ROCK_FALL_VY);
                        // Rock 3: 75% (ร่วงตรง)
                        parentFrame.spawnRock(rockX3, rockY, 0, GameConstants.ROCK_FALL_VY);
                        
                        currentAnimFrame = 99;
                    }
                    currentState = GolemState.RECHARGE;
                    lastActionTime = System.currentTimeMillis();
                }
                break;

            case RECHARGE: // พัก (Fair Play Logic)
                vx = 0;
                long waitTime = isPhase2 ? RECHARGE_DURATION_PHASE2 : RECHARGE_DURATION_PHASE1;
                if (System.currentTimeMillis() > lastActionTime + waitTime) {
                    currentState = GolemState.IDLE; // กลับไป IDLE เพื่อเลือกท่าใหม่
                }
                break;
                
        }
    }
    
    // Logic การแสดงผล Sprite/Animation
    private void updateVisuals() {
        // ซ่อน Attack และ Particle Labels ก่อน
        attackLabel.setVisible(false);
        particleLabel.setVisible(false);
        particleLabel2.setVisible(false);
        jumpLabel.setVisible(false);
        this.setVisible(true);
        
        if (System.currentTimeMillis() < flashTime) {
            this.setVisible(true);
            setIcon(hitGif);
            return;
        } else if (System.currentTimeMillis() >= flashTime) {
        }

        switch (currentState) {
            case IDLE:
                this.setVisible(true);
                setIcon(idleGif);
                break;
            case RECHARGE:
                this.setVisible(true);
                 setIcon(idleGif); 
                break;
            case ATTACK1:
                setAttackVisual(turnLeft ? attack1Flip : attack1Gif);
                break;
            case PREJUMP:
                this.setVisible(false);
                jumpLabel.setVisible(true);
                jumpLabel.setIcon(preJumpGif);
                jumpLabel.setBounds(curX , curY - (jump_height - height) , jump_width, jump_height);
                break;
            case ONAIR:
                this.setVisible(false); // <-- ใส่ผิดที่!
                 jumpLabel.setVisible(true);
                jumpLabel.setIcon(onAirGif);
                 jumpLabel.setBounds(curX, curY - (jump_height - height), jump_width, jump_height);
                break;
            case FALL:
                this.setVisible(false); // ซ่อนตัว Golem หลัก
                jumpLabel.setVisible(true);
                jumpLabel.setIcon(fallGif);
                jumpLabel.setBounds(curX, curY - (jump_height - height), jump_width, jump_height);
                break;
            case GROUNDHIT:
                this.setVisible(true);
                setIcon(groundHitGif);
                // แสดง Ground Particle
                particleLabel.setVisible(true);
                particleLabel.setIcon(particleGif);
                // ตำแหน่ง Particle ที่พื้น (ต้องอยู่ใต้ตัว Golem)
                particleLabel.setBounds(curX - 50, curY + height - (atk_height/2) + 20, atk_width + 100, atk_height / 2);
                break;
            case PRESLIDE:
                this.setVisible(true);
                setIcon(slideGif); // ใช้ภาพ IDLE เพื่อแสดงว่ากำลังชาร์จ
                break;
            case SLIDE:
                this.setVisible(true);
                setIcon(slideGif);
                // TODO: สามารถเพิ่ม Slide Particle Label ได้ที่นี่
                particleLabel.setVisible(true);
                particleLabel.setIcon(turnLeft ? particleSlideFlip:particleSlide);
                 particleLabel.setVisible(true);
                particleLabel.setIcon(turnLeft ? particleSlideFlip : particleSlide);
    
                // แสดง Slide Particle ที่มือขวา
                particleLabel2.setVisible(true);
                particleLabel2.setIcon(turnLeft ? particleSlideFlip : particleSlide);

                if (turnLeft) {
                    // พุ่งไปทางซ้าย
                    particleLabel.setBounds(curX + 30 , curY + height - 25, width, 35);  // มือซ้าย
                    particleLabel2.setBounds(curX + 190, curY + height - 25, width, 35); // มือขวา
                } else {
                    // พุ่งไปทางขวา
                    particleLabel.setBounds(curX - 180, curY + height - 25, width, 35);  // มือซ้าย
                    particleLabel2.setBounds(curX , curY + height - 25, width, 35); // มือขวา
                }
                break;
            case SLIDE_END:
                this.setVisible(true);
                setIcon(slideGif); // ค้างภาพ Slide ก่อนพัก
                break;
        }
    }
    
    // Helper function สำหรับแสดงผลท่าโจมตีที่ขยายขนาด
    private void setAttackVisual(GameIcon icon) {
        this.setVisible(false);
        attackLabel.setVisible(true);
        attackLabel.setIcon(icon);
        
        if (turnLeft) {
            attackLabel.setBounds(curX -(atk_width - width), curY - (atk_height - height), atk_width, atk_height);
        } else {
            attackLabel.setBounds(curX, curY - (atk_height - height), atk_width, atk_height);
        }
    }
    
    // สำหรับ Golem
    private void setDead() {
        this.setVisible(true);
        attackLabel.setVisible(false);
        particleLabel.setVisible(false);
        jumpLabel.setVisible(false);
        particleLabel2.setVisible(false);
        setIcon(deadGif);
        if (deathTimeEnd == 0) { 
            deathTimeEnd = System.currentTimeMillis() + (3*GameConstants.DURATION_MS);
        }
    }
    
    public boolean isDeathAnimationFinished() {
        return isDead && System.currentTimeMillis() > deathTimeEnd;
    }
    
    // ตรวจสอบว่า Golem กำลังทำดาเมจจากการชน (Move 2, 3)
    public boolean isCollisionDamageActive() {
        return true;
    }
    
    // ตรวจสอบว่า Golem กำลังใช้ท่าโจมตีระยะประชิด (Move 1)
    public boolean isAttack1Active() {
        return currentState == GolemState.ATTACK1;
    }

    // ตรวจสอบว่า Ground Particle ทำดาเมจ (Move 2)
    public boolean isParticleDamageActive() {
        return currentState == GolemState.GROUNDHIT;
    }
    
    public void stopAllSounds() {
        if (attackSFX != null) attackSFX.stop();
        if (jumpSFX != null) jumpSFX.stop();
        if (landSFX != null) landSFX.stop();
        if (slideSFX != null) slideSFX.stop();
    }
}