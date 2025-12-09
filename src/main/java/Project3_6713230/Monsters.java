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

public abstract class Monsters extends JLabel{
    
    protected GameFrame parentFrame;
    protected GameMap map;
    
    protected int width;
    protected int height;
    protected int curX;
    protected int curY;
    protected int hp;
    protected int attackPoint;
    protected int speed;
    protected String difficulty;
    
    protected int vx = 0;
    protected int vy = 0;
    protected int gravity = GameConstants.GRAVITY;
    protected final int CHASE_RANGE = 300;
    protected final int ATTACK_RANGE = 50;
    protected boolean isAttacking = false;
    protected boolean onGround = true;
    protected boolean turnLeft = true;
    protected long deathTimeEnd = 0;
    protected long hitTime = 0;
    protected boolean isHit = false;
    protected long attackTimeStart = 0;
    protected long attackTimeEnd = 0;
    protected boolean hitboxActive = false;
    
    protected JLabel attack;
    protected int atk_width;
    protected int atk_height;
    
    protected GameIcon idleGif, walkGif, deadGif, attackGif, hitGif;
    protected GameIcon idleFlip, walkFlip, deadFlip, attackFlip, hitFlip;

    public Monsters(GameFrame pf, GameMap m, int startX, int startY, String gameDifficulty,
                   int monsterWidth, int monsterHeight, int atkWidth, int atkHeight, 
                   int initialHP, int initialAttack, int initialSpeed) {
        
        this.parentFrame = pf;
        this.map = m;
        this.curX = startX;
        this.curY = startY;
        
        this.width = monsterWidth;
        this.height = monsterHeight;
        this.atk_width = atkWidth;
        this.atk_height = atkHeight;
        this.hp = initialHP;
        this.attackPoint = initialAttack;
        this.speed = initialSpeed;
        this.difficulty = gameDifficulty;
        
        this.setLayout(null);
        
        attack = new JLabel();
        attack.setVisible(false);
        //attack.setBorder(BorderFactory.createLineBorder(Color.PINK, 2));

        setBounds(curX, curY, width, height);
        //setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    public Rectangle getHitBox()                { return new Rectangle(curX, curY, width, height); }
    public int getHp()                          { return hp; }
    public int getAttackPoint()                 { return attackPoint; }
    public boolean isAttacking()                { return isAttacking; }
    public boolean isHitboxActive()             { return hitboxActive; }
    public boolean isDead()                     { return hp <= 0; }
    public long getAttackTimeEnd()              { return attackTimeEnd; }
    public JLabel getAttackLabel()              { return attack; }
    public boolean isDeathAnimationFinished()   { return isDead() && System.currentTimeMillis() > deathTimeEnd; }
    
    protected abstract void playIdleSound();
    protected abstract void playAttackSound();
    protected abstract void playWalkSound();
    protected abstract void playHitSound();
    protected abstract void playDeadSound();
    public abstract void stopAllSounds();
    
    // --- Movement and Physics
    public void updateLocation() {
        if (isDead()) {
            setDead();
            vx = 0;
            vy = 0;
            setLocation(curX, curY);
            return;
        }
        
        final int OUT_OF_BOUNDS_Y = GameConstants.FRAME_HEIGHT;
        final int OUT_OF_BOUNDS_X = GameConstants.FRAME_WIDTH - width; 

        if (curY > OUT_OF_BOUNDS_Y) {
            if (!isDead()) {
                hp = 0;
                setDead();
            }
        }
        
        if (curX < 0) {
            curX = 0;
            vx = 0;
        } else if (curX > OUT_OF_BOUNDS_X) {
            curX = OUT_OF_BOUNDS_X;
            vx = 0;
        }
        
        vy += gravity;

        int nextY = curY + vy;
        int checkY_top = curY + 5;
        
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

        boolean leftFoot  = map.isSolid(curX + 10, nextY + height);
        boolean rightFoot = map.isSolid(curX + width - 10, nextY + height);

        if (vy >= 0 && (leftFoot || rightFoot)) {
            vy = 0;
            onGround = true;

            int tileRow = (nextY + height) / map.TILE_SIZE;
            curY = (tileRow * map.TILE_SIZE) - height;
        }
        else {
            curY = nextY;
            onGround = false;
        }

        int nextX = curX + vx;
        int checkY_middle = curY + height / 2;
        boolean leftWallHit = map.isSolid(nextX, checkY_middle);
        boolean rightWallHit = map.isSolid(nextX + width, checkY_middle);
        
        boolean isKnockedBack = System.currentTimeMillis() < hitTime;
        
        if (vx > 0) {
            if (rightWallHit) {
                vx = 0;
            } 

            if (rightWallHit && isKnockedBack) {
                vx = 0;
                turnLeft = true;
            } else if (!rightWallHit) {
                curX = nextX;
            }

        } else if (vx < 0) {
            if (leftWallHit) {
                vx = 0;
            }

            if (leftWallHit && isKnockedBack) {
                vx = 0;
                turnLeft = false;
            } else if (!leftWallHit) {
                curX = nextX;
            }
        }

        setLocation(curX, curY);
    }
    
    // --- Combat Logic
    public void damage(int d, boolean isAttackerFacingLeft) {
        if (isDead()) return;
        if (System.currentTimeMillis() < hitTime){
            stopAllSounds();
            setHit();
            return;
        }
        hp -= d;
        isAttacking = false;
        if (isDead()) {
            hp = 0;
            vx = 0;
            vy = 0;
            setDead();
        }else {
            hitTime = System.currentTimeMillis() + GameConstants.DURATION_MS;
            isHit = true;
            stopAllSounds();
            setHit();
            
            int kb = GameConstants.KNOCKBACK;
            vx = (isAttackerFacingLeft ? -kb : kb);
            vy = -kb;
        }
    }
    
    // --- Behavior Logic
    public void updateBehavior(Knight player) {
        long now = System.currentTimeMillis();
        
        if (isDead()) {
            setDead();
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
        int absDistanceX = Math.abs(distanceX);
        int distanceY = Math.abs(player.getCurY() - curY);
        
        int nextX = turnLeft ? curX - speed : curX + width + speed;
        int nextY = curY + height;
        
        boolean groundAhead = map.isSolid(nextX, nextY + 1);

        boolean hazardAhead = map.isHazard(nextX, nextY) || map.isLethal(nextX, nextY);
                
        if (onGround) {
            if (!groundAhead || hazardAhead) {                
                turnLeft = !turnLeft;
                if (turnLeft) {
                    vx = -speed;
                } else {
                    vx = speed;
                }
                return;
            }
        }

        if (absDistanceX < ATTACK_RANGE && distanceY < height) {
            if (!isAttacking) {
                isAttacking = true;
                attackTimeStart = now + (2*GameConstants.DURATION_MS);
                attackTimeEnd = now + (4*GameConstants.DURATION_MS);
            }
            vx = 0;

        } else if (absDistanceX < CHASE_RANGE && distanceY < height && groundAhead && !hazardAhead) {
            if (distanceX < 0) {
                turnLeft = true;
                vx = -speed;
            } else {
                turnLeft = false;
                vx = speed;
            }
        } else if(!groundAhead || hazardAhead){
            if (turnLeft) {
                vx = -speed;
            } else {
                vx = speed;
            }
        }else{
            vx = 0;
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

    // --- Animation Setters
    protected void setWalk() {
        if (isDead()) {
            setDead();
            return;
        }
        this.setVisible(true);
        attack.setVisible(false);
        playWalkSound();
        if (turnLeft) {
            setIcon(walkFlip);
        } else {
            setIcon(walkGif);
        }
    }
    
    protected void setIdle() {
        this.setVisible(true);
        attack.setVisible(false);
        if (turnLeft) {
            setIcon(idleFlip);
        } else {
            setIcon(idleGif);
        }
        if(getIcon() == null) {
            setIcon(idleGif);
        }
    }
    
    public void setDead() {
        this.setVisible(true);
        attack.setVisible(false);
        stopAllSounds();
        playDeadSound();
        if (turnLeft) {
            setIcon(deadFlip);
        } else {
            setIcon(deadGif);
        }
        if (deathTimeEnd == 0) {
            deathTimeEnd = System.currentTimeMillis() + (3*GameConstants.DURATION_MS);
        }
    }
    
    public void setAttack() {
        this.setVisible(false);
        attack.setVisible(true);
        stopAllSounds();
        playAttackSound();
        if (turnLeft) {
              attack.setIcon(attackFlip);
              attack.setBounds(curX -(atk_width - width),curY - (atk_height - height),atk_width, atk_height);

        } else {
              attack.setIcon(attackGif);
              attack.setBounds(curX,curY - (atk_height - height),atk_width, atk_height);
        }
    }
    
    public void setHit() {
        stopAllSounds();
        playHitSound();
        if (turnLeft) {
            setIcon(hitFlip);
        } else {
            setIcon(hitGif);
        }
    }
    
}