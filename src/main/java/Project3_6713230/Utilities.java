/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.sound.sampled.*;

interface GameConstants
{
    //----- Resource files
    static final String FILE_BG_MAP1     = "Levels/tilebase1.jpg";
    static final String FILE_BG_MAP2     = "Levels/tilebase2.jpg";
    static final String FILE_BG_MAP3     = "Levels/tilebase3.jpg";
    static final String FILE_BG_MAP4     = "Levels/tilebase4.jpg";
    static final String FILE_BG_MAP5     = "Levels/tilebase5.jpg";
    static final String FILE_BG_MAP6     = "Levels/tilebase6.jpg";
    static final String FILE_POTION      = "/Levels/potion.png";

    static final String FILE_THEME                  = "Sound/CardCastle.wav";
    static final String FILE_BOSS_THEME             = "Sound/BossTheme.wav";
    static final String FILE_MENU_THEME             = "Sound/Menu.wav";
    static final String FILE_MC_ATTACK_SFX          = "Sound/Knight/sword-swing.wav";
    static final String FILE_MC_ATTACK3_SFX         = "Sound/Knight/attack3.wav";
    static final String FILE_MC_RUN_SFX             = "Sound/Knight/running.wav";
    static final String FILE_MC_ROLL_SFX            = "Sound/Knight/roll.wav";
    static final String FILE_MC_HIT_SFX             = "Sound/Knight/hit.wav";
    static final String FILE_MC_HEAL_SFX            = "Sound/Knight/heal.wav";
    static final String FILE_MC_DEAD_SFX            = "Sound/Knight/dead.wav";
    static final String FILE_MC_NOPE_SFX            = "Sound/Knight/nope.wav";
    static final String FILE_SKELETON_ATTACK_SFX    = "Sound/Monsters/Skeleton/Attack.wav";
    static final String FILE_SKELETON_WALK_SFX      = "Sound/Monsters/Skeleton/Walk.wav";
    static final String FILE_SKELETON_HIT_SFX       = "Sound/Monsters/Skeleton/Hit.wav";
    static final String FILE_SKELETON_DEAD_SFX      = "Sound/Monsters/Skeleton/Dead.wav";
    static final String FILE_BAT_IDLE_SFX           = "Sound/Monsters/Bat/Idle.wav";
    static final String FILE_BAT_HIT_SFX            = "Sound/Monsters/Bat/Hit.wav";
    static final String FILE_BAT_DEAD_SFX           = "Sound/Monsters/Bat/Dead.wav";
    static final String FILE_GOLEM_ATTACK_SFX   = "Sound/Boss/Attack.wav";
    static final String FILE_GOLEM_JUMP_SFX     = "Sound/Boss/Jump.wav";
    static final String FILE_GOLEM_LAND_SFX     = "Sound/Boss/Land.wav";
    static final String FILE_GOLEM_SLIDE_SFX    = "Sound/Boss/Slide.wav";
    static final String FILE_GOLEM_HIT_SFX      = "Sound/Boss/Hit.wav";
    static final String FILE_GOLEM_DEAD_SFX     = "Sound/Boss/Dead.wav";
    static final String FILE_ROCK_SMASH_SFX     = "Sound/Boss/Rocksmash.wav";
    static final String FILE_VICTORY_SFX          = "Sound/Victory.wav";

    static final String FILE_MC_IDLE     = "/Sprite/Knight/mc_Idle.gif";
    static final String FILE_MC_IDLEFLIP = "/Sprite/Knight/mc_IdleFlip.gif";
    static final String FILE_MC_RUN      = "/Sprite/Knight/mc_Run.gif";
    static final String FILE_MC_RUNFLIP  = "/Sprite/Knight/mc_RunFlip.gif";
    static final String FILE_MC_JUMP     = "/Sprite/Knight/mc_Jump.gif";
    static final String FILE_MC_JUMPFLIP = "/Sprite/Knight/mc_JumpFlip.gif";
    static final String FILE_MC_ROLL     = "/Sprite/Knight/mc_Roll.gif";
    static final String FILE_MC_ROLLFLIP = "/Sprite/Knight/mc_RollFlip.gif";
    static final String FILE_MC_ATTACK1  = "/Sprite/Knight/mc_Attack1.gif";
    static final String FILE_MC_ATTACK1FLIP     = "/Sprite/Knight/mc_Attack1Flip.gif";
    static final String FILE_MC_ATTACK2         = "/Sprite/Knight/mc_Attack2.gif";
    static final String FILE_MC_ATTACK2FLIP     = "/Sprite/Knight/mc_Attack2Flip.gif";
    static final String FILE_MC_ATTACK3         = "/Sprite/Knight/mc_AttackCombo2hit.gif";
    static final String FILE_MC_ATTACK3FLIP     = "/Sprite/Knight/mc_AttackCombo2hitFlip.gif";
    static final String FILE_MC_HIT             = "/Sprite/Knight/mc_Hit.gif";
    static final String FILE_MC_HITFLIP         = "/Sprite/Knight/mc_HitFlip.gif";
    static final String FILE_MC_DEAD            = "/Sprite/Knight/mc_Dead.gif";
    static final String FILE_MC_DEADFLIP        = "/Sprite/Knight/mc_DeadFlip.gif";

    static final String FILE_SKELETON_ATTACK       = "/Sprite/Monsters/Skeleton/Attack.gif";
    static final String FILE_SKELETON_IDLE         = "/Sprite/Monsters/Skeleton/Idle.gif";
    static final String FILE_SKELETON_WALK         = "/Sprite/Monsters/Skeleton/Walk.gif";
    static final String FILE_SKELETON_HIT          = "/Sprite/Monsters/Skeleton/Onehit.gif";
    static final String FILE_SKELETON_DEAD         = "/Sprite/Monsters/Skeleton/Dead.gif";
    static final String FILE_SKELETON_ATTACKFLIP   = "/Sprite/Monsters/Skeleton/Attackflip.gif";
    static final String FILE_SKELETON_IDLEFLIP     = "/Sprite/Monsters/Skeleton/Idleflip.gif";
    static final String FILE_SKELETON_WALKFLIP     = "/Sprite/Monsters/Skeleton/Walkflip.gif";
    static final String FILE_SKELETON_HITFLIP      = "/Sprite/Monsters/Skeleton/Onehitflip.gif";
    static final String FILE_SKELETON_DEADFLIP     = "/Sprite/Monsters/Skeleton/Deadflip.gif";

    static final String FILE_GOLEM_IDLE       = "/Sprite/Boss/Idle.gif";
    static final String FILE_GOLEM_ATTACK1       = "/Sprite/Boss/Attack1.gif";
    static final String FILE_GOLEM_ATTACK1FLIP       = "/Sprite/Boss/Attack1flip.gif";
    static final String FILE_GOLEM_PREJUMP       = "/Sprite/Boss/prejump.gif";
    static final String FILE_GOLEM_ONAIR         = "/Sprite/Boss/onair.png";
    static final String FILE_GOLEM_FALL          = "/Sprite/Boss/fall.png";
    static final String FILE_GOLEM_GROUNDHIT     = "/Sprite/Boss/groundHit.png";
    static final String FILE_GOLEM_PARTICLE      = "/Sprite/Boss/particle.gif";
    static final String FILE_GOLEM_ROCK        = "/Sprite/Boss/rock.png";
    static final String FILE_GOLEM_ROCKPARTICLE        = "/Sprite/Boss/rockParticle.png";
    static final String FILE_GOLEM_SLIDE         = "/Sprite/Boss/slide.png";
    static final String FILE_GOLEM_SLIDEPARTICLE = "/Sprite/Boss/slideParticle.gif";
    static final String FILE_GOLEM_SLIDEPARTICLEFLIP = "/Sprite/Boss/slideParticleflip.gif";
    static final String FILE_GOLEM_HIT_SPRITE    = "/Sprite/Boss/hit.png";
    static final String FILE_GOLEM_DEAD    = "/Sprite/Boss/dead.png";

    static final String FILE_BAT_IDLE              = "/Sprite/Monsters/Bat/Idle.gif";
    static final String FILE_BAT_DEAD              = "/Sprite/Monsters/Bat/Dead.gif";
    static final String FILE_BAT_IDLEFLIP          = "/Sprite/Monsters/Bat/Idleflip.gif";
    static final String FILE_BAT_DEADFLIP          = "/Sprite/Monsters/Bat/Deadflip.gif";

    static final String FILE_TITLE     = "/UI/Title.gif";
    static final String FILE_CASTLE_BG     = "/BG/Castle.gif";
    static final String FILE_FOREST2_BG     = "/BG/Forest2.gif";
    static final String FILE_FOREST1_BG     = "/BG/Forest1.gif";
    static final String FILE_RAIN_BG     = "/BG/Rain.gif";

    static final int FRAME_WIDTH  = 960;
    static final int FRAME_HEIGHT = 640;
    static final int TILE_SIZE = 32;
    
    static final int MC_WIDTH = 45;
    static final int MC_HEIGHT = 80;

    static final int MC_ROLL_WIDTH = 100;
    static final int MC_ROLL_HEIGHT = 80;
    static final int MC_ATK_WIDTH = 120;
    static final int MC_ATK_HEIGHT = 110;
    
    static final int SKELETON_WIDTH  = 50;
    static final int SKELETON_HEIGHT = 80;
    static final int SKELETON_ATK_WIDTH  = 80;
    static final int SKELETON_ATK_HEIGHT = 95;
    
    static final int ROCK_WIDTH          = 100;
    static final int ROCK_HEIGHT         = 100;
    static final int ROCK_FALL_VY = 3;
    static final int ROCK_PROJECTILE_VX = 10;
    static final int PARTICLE_DURATION_MS = 1000;
    
    final int        GOLEM_WIDTH         = 230;
    static final int GOLEM_HEIGHT        = 170;
    static final int GOLEM_ATK_WIDTH     = 270;
    static final int GOLEM_ATK_HEIGHT    = 170;
    static final int GOLEM_JUMP_WIDTH     = 290;
    static final int GOLEM_JUMP_HEIGHT    = 240;
    static final int GOLEM_MAX_HP        = 1000;
    static final int GOLEM_MELEE_DMG = 25;
    static final int GOLEM_JUMP_SLIDE_DMG= 25;
    static final int GOLEM_ROCK_DMG      = 25;
    static final int GOLEM_RECHARGE_TIME = 1500;
    
    static final int BAT_WIDTH = 50;
    static final int BAT_HEIGHT = 50;
    static final int BAT_ATK_WIDTH = 40;
    static final int BAT_ATK_HEIGHT = 40;
    
    static final int KNOCKBACK = 3;
    static final int FRAME_RATE_MS = 16; //Timer

    static final int GRAVITY = 1;
    static final int DURATION_MS = 500; //Delay
}

class GameIcon extends ImageIcon
{
    public GameIcon(String fname)  {
        ImageIcon loadedIcon = AssetLoader.loadGifIcon(fname);

        if (loadedIcon != null) {
            System.out.println("Loaded sprite: " + fname);
            setImage(loadedIcon.getImage());
            setDescription(loadedIcon.getDescription());
        } else {
            System.err.println("Sprite loading failed for: " + fname);
        }
    }
    public GameIcon(Image image)   { super(image); }

    public GameIcon resize(int width, int height)
    {
        Image oldimg = this.getImage();
        Image newimg = oldimg.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
        return new GameIcon(newimg);
    }
}

class GameSoundEffect
{
    private Clip clip;
    private FloatControl gainControl;
    public static float masterVolume = 0.5f;

    public GameSoundEffect(String filename) {
        this(AssetLoader.loadClip(filename));
    }

    private GameSoundEffect(Clip loadedClip) {
        this.clip = loadedClip;
        if (clip != null) {
            try {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    setVolume(masterVolume);
                } else {
                    System.err.println("Warning: Master Gain control not supported for this sound.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Could not initialize gain control.");
                e.printStackTrace();
            }
        }
    }
    
    public void playOnce()
    {

                    if (clip != null) {
                        if (clip.isRunning()) clip.stop();
                        clip.setMicrosecondPosition(0);
                        clip.start();
                    }

    }
    
    public void playLoop()
    { 

                    if (clip != null) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    }

    }
    
    public void stop()
    { 
        if (clip != null) {
            clip.stop();
        }
    }
            
    public void setVolume(float gain)
    {
        if (gainControl != null) {
            if (gain < 0.0f)  gain = 0.0f;
            if (gain > 1.0f)  gain = 1.0f;
            float dB = (float)(Math.log(gain) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}