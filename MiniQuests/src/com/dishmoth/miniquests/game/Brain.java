/*
 *  Brain.java
 *  Copyright Simon Hern 2011
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.LinkedList;

// different control instructions for the player (keys by default)
public class Brain {

  // functionality supported by different brains 
  public static abstract class Module {
    protected boolean mRight=false, mUp=false, mLeft=false, mDown=false, 
                      mFire=false, mFinished=false;

    public boolean right()    { return mRight;    }
    public boolean up()       { return mUp;       }
    public boolean left()     { return mLeft;     }
    public boolean down()     { return mDown;     }
    public boolean fire()     { return mFire;     }
    public boolean finished() { return mFinished; }

    public abstract void advance();
  } // class Brain.Module
  
  // the default brain simply reads the keyboard 
  public static class KeyModule extends Module {
    public void advance() {
      mRight = Env.keys().right();
      mUp    = Env.keys().up();
      mLeft  = Env.keys().left();
      mDown  = Env.keys().down();
      mFire  = Env.keys().fire();
    } // advance()
  } // class Brain.KeyModule
  
  // brain to read the keyboard, except fire 
  public static class NoFireModule extends Module {
    public void advance() {
      mRight = Env.keys().right();
      mUp    = Env.keys().up();
      mLeft  = Env.keys().left();
      mDown  = Env.keys().down();
      mFire  = false;
    } // advance()
  } // class Brain.NoFireModule
  
  // brain that follows a list of instructions
  // format: { direc, count, direc, count, ... }
  public static class ZombieModule extends Module {
    private int mInstructions[];
    private int mIndex, mCounter;
    
    public ZombieModule(int instructions[]) {
      assert( instructions != null && (instructions.length % 2) == 0 );
      mInstructions = instructions;
      mIndex = mCounter = 0;
    }
    
    public void advance() {
      assert( !mFinished );
      final int direc = mInstructions[2*mIndex],
                count = mInstructions[2*mIndex+1];
      assert( direc >= -1 && direc < 4 );
      mRight = ( direc == Env.RIGHT );
      mUp    = ( direc == Env.UP    );
      mLeft  = ( direc == Env.LEFT  );
      mDown  = ( direc == Env.DOWN  );
      mFire  = false;
      if ( ++mCounter >= count ) {
        mCounter = 0;
        if ( ++mIndex >= mInstructions.length/2 ) mFinished = true;
      }
    }
  } // class Brain.ZombieModule 
  
  // stack of brains, the active one is at the end of the list
  private LinkedList<Module> mBrainStack;
  
  // constructor
  public Brain() {
    
    mBrainStack = new LinkedList<Module>();
    mBrainStack.add( new KeyModule() );
    
  } // constructor

  // update the brain
  public void advance() {

    Module brain = mBrainStack.getLast(); 
    brain.advance();
    
    if ( brain.finished() ) mBrainStack.removeLast(); 
    assert( mBrainStack.size() > 0 );
    
  } // advance()
  
  // current state of the brain
  public boolean right() { return mBrainStack.getLast().right(); }
  public boolean up()    { return mBrainStack.getLast().up();    }
  public boolean left()  { return mBrainStack.getLast().left();  }
  public boolean down()  { return mBrainStack.getLast().down();  }
  public boolean fire()  { return mBrainStack.getLast().fire();  }

  // push a new brain on the stack
  public void add(Module newBrain) {
    
    assert( newBrain != null );
    mBrainStack.add( newBrain );
    
  } // add()

  // pop the active brain from the stack
  public void remove() {
    
    mBrainStack.removeLast();
    assert( mBrainStack.size() > 0 );
    
  } // remove()
  
} // class Brain
