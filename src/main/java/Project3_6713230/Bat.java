/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

public class Bat extends Monsters {
    
    private static final int BAT_WIDTH = GameConstants.BAT_WIDTH;
    private static final int BAT_HEIGHT = GameConstants.BAT_HEIGHT;
    private static final int BAT_ATK_WIDTH = GameConstants.BAT_ATK_WIDTH;
    private static final int BAT_ATK_HEIGHT = GameConstants.BAT_ATK_HEIGHT;
    private static int INITIAL_HP = 150;
    private static int ATTACK_POINT = 15;
    private static int SPEED = 3;
    
    protected GameSoundEffect idleSFX, hitSFX, deadSFX;
    
    public Bat(GameFrame pf, GameMap m, int startX, int startY, String gameDifficulty) {
        
        super(pf, m, startX, startY, gameDifficulty,
              BAT_WIDTH, BAT_HEIGHT, 
              BAT_ATK_WIDTH, BAT_ATK_HEIGHT, 
              INITIAL_HP, ATTACK_POINT, SPEED);
        
        setInitialStats();

        this.gravity = 0; 

        idleGif    = new GameIcon(GameConstants.FILE_BAT_IDLE).resize(width, height);
        deadGif    = new GameIcon(GameConstants.FILE_BAT_DEAD).resize(width, height);

        idleFlip   = new GameIcon(GameConstants.FILE_BAT_IDLEFLIP).resize(width, height);
        deadFlip   = new GameIcon(GameConstants.FILE_BAT_DEADFLIP).resize(width, height);
        
        idleSFX    = new GameSoundEffect(GameConstants.FILE_BAT_IDLE_SFX);
        hitSFX     = new GameSoundEffect(GameConstants.FILE_BAT_HIT_SFX);
        deadSFX    = new GameSoundEffect(GameConstants.FILE_BAT_DEAD_SFX);
        
        setIcon(idleGif);
    }
    
    private void setInitialStats() {
        if ("Easy".equals(difficulty)) {
            INITIAL_HP = 100;
            ATTACK_POINT = 10;
            SPEED = 2;
        } else if ("Normal".equals(difficulty)) {
            INITIAL_HP = 150;
            ATTACK_POINT = 15;
            SPEED = 3;
        } else if ("Hard".equals(difficulty)) {
            INITIAL_HP = 200;
            ATTACK_POINT = 25;
            SPEED = 4;
        } else if ("Expert".equals(difficulty)) {
            INITIAL_HP = 300;
            ATTACK_POINT = 35;
            SPEED = 4;
        } else if ("Nightmare".equals(difficulty)) {
            INITIAL_HP = 400;
            ATTACK_POINT = 50;
            SPEED = 5;
        }
    }
    
    @Override
    public void updateLocation() {
        if (isDead()) {
            setDead();
            vx = 0;
            vy = 0;
            setLocation(curX, curY);
            return;
        }
        
        final int OUT_OF_BOUNDS_Y = GameConstants.FRAME_HEIGHT;
        final int OUT_OF_BOUNDS_X = GameConstants.FRAME_WIDTH; 

        if (curY > OUT_OF_BOUNDS_Y || curX < -OUT_OF_BOUNDS_X || curX > 960 + OUT_OF_BOUNDS_X) {
            if (!isDead()) {
                hp = 0;
                setDead();
            }
        }
        
        if (curY < 0) {
            curY = 0;
            vy = 0;
        }

        int nextX = curX + vx;
        int nextY = curY + vy;

        int checkX_center = curX + width / 2;
        int checkY_middle = curY + height / 2;

        boolean isKnockedBack = System.currentTimeMillis() < hitTime;

        if (vx != 0) {

            int checkX_left = nextX;
            int checkX_right = nextX + width;

            boolean leftWallHit = map.isSolid(checkX_left, checkY_middle) || map.isLethal(checkX_left, checkY_middle);
            boolean rightWallHit = map.isSolid(checkX_right, checkY_middle) || map.isLethal(checkX_right, checkY_middle);

            if (vx > 0) {
                if (rightWallHit) {
                    vx = 0;
                    if (!isKnockedBack) {
                        turnLeft = true;
                    }
                } else {
                    curX = nextX;
                }
            } else if (vx < 0) {
                if (leftWallHit) {
                    vx = 0;
                    if (!isKnockedBack) {
                        turnLeft = false;
                    }
                } else {
                    curX = nextX;
                }
            }
        }

        if (vy != 0) {

            int checkY_top = nextY;
            int checkY_bottom = nextY + height;

            boolean topHit = map.isSolid(checkX_center, checkY_top) || map.isLethal(checkX_center, checkY_top);
            boolean bottomHit = map.isSolid(checkX_center, checkY_bottom) || map.isLethal(checkX_center, checkY_bottom);

            if (vy < 0) {
                if (topHit) {
                    vy = 0;
                } else {
                    curY = nextY;
                }
            } else if (vy > 0) {
                if (bottomHit) {
                    vy = 0;
                } else {
                    curY = nextY;
                }
            }
        }

        if (vx != 0 || vy != 0) {
            setWalk(); 
        } else {
            setIdle();
        }

        setLocation(curX, curY);
    }

    @Override
    public void updateBehavior(Knight player) {
        long now = System.currentTimeMillis();
        
        if (isDead()) {
            setDead();
            stopAllSounds();
            return;
        }   
        
        if (isHit) {
            if (now < hitTime) {
                return;
            }
            isHit = false;
        }

        int playerCenterX = player.getCurX() + player.getWidth() / 2;
        int monsterCenterX = curX + width / 2;
        int distanceX = playerCenterX - monsterCenterX;
        int absDistanceX = Math.abs(distanceX) + 10;
        
        int playerHeadY = player.getCurY();
        int monsterCenterY = curY + height / 2;
        int distanceY = playerHeadY - monsterCenterY;
        int absDistanceY = Math.abs(distanceY) + 30;

        if (absDistanceX < ATTACK_RANGE && absDistanceY < height) {
            if (!isAttacking) {
                isAttacking = true;
                attackTimeStart = now + (2*GameConstants.DURATION_MS);
                attackTimeEnd = now + (4*GameConstants.DURATION_MS);
            }
            vx = 0;
            vy = 0;

        } else if (absDistanceX < CHASE_RANGE && absDistanceY < CHASE_RANGE) {
            
            if (distanceX < 0) {
                turnLeft = true;
                vx = -speed;
            } else {
                turnLeft = false;
                vx = speed;
            }
            
            if (distanceY < 0) {
                vy = -speed;
            } else {
                vy = speed;
            }
            
        } else {
            vx = 0;
            vy = 0;
        }
        
        if (isAttacking) {
            if (now >= attackTimeStart && now <= attackTimeEnd) {
                hitboxActive = true;
            } else {
                hitboxActive = false;
            }
            
            if (now > attackTimeEnd) {
                isAttacking = false;
                attack.setVisible(false);
                hitboxActive = false;
            } else {
                setAttack();
                return;
            }
        }else {
            if (vx != 0) {
                setWalk();
            } else {
                setIdle();
            }
        }
    }
    
    @Override
    protected void setWalk() {
        if (isDead()) {
            setDead();
            return;
        }
        this.setVisible(true);
        attack.setVisible(false);
        playIdleSound();
        if (turnLeft) {
            setIcon(idleFlip);
        } else {
            setIcon(idleGif);
        }
    }
    
    @Override
    public void stopAllSounds(){
        idleSFX.stop();
    }
    
    @Override
    protected void playIdleSound() {
        if (idleSFX != null) {
            idleSFX.playLoop();
            idleSFX.setVolume(0.3f * GameSoundEffect.masterVolume);
        }
    }
    
    @Override
    protected void playAttackSound() {}
    
    @Override
    protected void playWalkSound() {}
    
    @Override
    protected void playHitSound() {
        stopAllSounds();
        if (hitSFX != null) {
            hitSFX.playOnce();
            hitSFX.setVolume(0.3f * GameSoundEffect.masterVolume);
        }
    }
    
    @Override
    protected void playDeadSound() {
        stopAllSounds();
        if (idleSFX != null) {
            deadSFX.playOnce();
            deadSFX.setVolume(0.3f * GameSoundEffect.masterVolume);
        }
    }
}