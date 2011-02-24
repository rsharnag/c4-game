package org.example.connectfour;
import java.util.Random;
import java.lang.System;
public class C4_game {
	private final byte C4_NONE=2;
	private final int C4_MAX_LEVEL=7;
	private boolean GAME_IN_PROGRESS=false;
	private boolean MOVE_IN_PROGRESS=false;
	private int size_x,size_y,num_to_connect;
	private int magic_win_number,win_places,depth,states_allocated;
	private int[][][] map;
	private int[] drop_order;
	private Game_state current_state;
	private Game_state[] state_stack;
	private Random random;
	public C4_game(int size_x,int size_y,int num )
	{
		int win_index;
		this.size_x=size_x;
		this.size_y=size_y;
		this.num_to_connect=num;
		assert !GAME_IN_PROGRESS;
		assert this.size_x >= 1 && this.size_y >= 1 && this.num_to_connect >= 1;
		random=new Random();
		magic_win_number=1<<this.num_to_connect;
		win_places=num_of_win_plcaes(this.size_x,this.size_y,this.num_to_connect);
		
		/* Set up the board*/
		state_stack = new Game_state[C4_MAX_LEVEL+1];
		for(int i=0;i<C4_MAX_LEVEL+1;i++)
		{
			this.state_stack[i]=new Game_state(this.size_x,this.size_y,this.win_places);
		}
		depth = 0;
	    current_state = state_stack[0];
	    for (int i=0; i<this.size_x; i++) {
	        for (int j=0; j<this.size_y; j++)
	            current_state.board[i][j] =C4_NONE;
	    }
	    /* Set up the score array */
	    for (int i=0; i<win_places; i++) {
	        current_state.score_array[0][i] = 1;
	        current_state.score_array[1][i] = 1;
	    }
	    
	    current_state.score[0] = current_state.score[1] = win_places;
	    current_state.winner = C4_NONE;
	    current_state.num_of_pieces = 0;
	    
	    states_allocated=1;
	    
	    /* Set up the map */

	    map = new int[this.size_x][this.size_y][this.num_to_connect*4+1];
	    for (int i=0; i<this.size_x; i++) {
	        for (int j=0; j<this.size_y; j++) {
	            map[i][j][0] = -1;
	        }
	    }
	    win_index = 0;
	    int x;
	    int[] win_indices;
	    /* Fill in the horizontal win positions */
	    for (int i=0; i<this.size_y; i++)
	        for (int j=0; j<this.size_x-this.num_to_connect+1; j++) {
	            for (int k=0; k<this.num_to_connect; k++) {
	                win_indices = map[j+k][i];
	                for (x=0; win_indices[x] != -1; x++);
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }

	    /* Fill in the vertical win positions */
	    for (int i=0; i<this.size_x; i++)
	        for (int j=0; j<this.size_y-this.num_to_connect+1; j++) {
	            for (int k=0; k<this.num_to_connect; k++) {
	                win_indices = map[i][j+k];
	                for (x=0; win_indices[x] != -1; x++);
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }
	    /* Fill in the forward diagonal win positions */
	    for (int i=0; i<this.size_y-this.num_to_connect+1; i++)
	        for (int j=0; j<this.size_x-this.num_to_connect+1; j++) {
	            for (int k=0; k<this.num_to_connect; k++) {
	                win_indices = map[j+k][i+k];
	                for (x=0; win_indices[x] != -1; x++);
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }

	    /* Fill in the backward diagonal win positions */
	    for (int i=0; i<this.size_y-this.num_to_connect+1; i++)
	        for (int j=this.size_x-1; j>=this.num_to_connect-1; j--) {
	            for (int k=0; k<this.num_to_connect; k++) {
	                win_indices = map[j-k][i+k];
	                for (x=0; win_indices[x] != -1; x++);
	                win_indices[x++] = win_index;
	                win_indices[x] = -1;
	            }
	            win_index++;
	        }

	    /* Set up the order in which automatic moves should be tried. */
	   
	    drop_order = new int[this.size_x];
	    int column = 3;
	    for (int i=1; i<=this.size_x; i++) {
	        drop_order[i-1] = column;
	        column += ((i%2)!=0? i : -i);
	    }

	    GAME_IN_PROGRESS = true;
	}
	public boolean C4_make_move(byte player,int column)
	{
		int result; 

	    assert GAME_IN_PROGRESS;
	    assert !MOVE_IN_PROGRESS;
	    //check invalid input
	    if (column >= size_x || column < 0)
	        return false;
	    //drop the piece
	    result = drop_piece(real_player(player), column);
	    return (result >= 0);
	}
	public int c4_auto_move(byte player, int level)
	{
		int column=0;
	    int i, best_column = -1, goodness = 0, best_worst = -(Integer.MAX_VALUE);
	    int num_of_equal = 0,  current_column; //result;
	    byte real_players;
	    assert GAME_IN_PROGRESS;
	    assert !MOVE_IN_PROGRESS;
	    assert (level >= 1 && level <= C4_MAX_LEVEL);

	    real_players = real_player(player);

	 
	    if (this.current_state.num_of_pieces < 2 &&
	                        this.size_x == 7 && this.size_y == 6 && this.num_to_connect == 4 &&
	                        (this.current_state.num_of_pieces == 0 ||
	                         this.current_state.board[3][0] != (char)C4_NONE)) {
	        column = 3;
	       // int row = current_state.num_of_pieces;
	        drop_piece(real_players, 3);
	        return column;
	    }

	    MOVE_IN_PROGRESS = true;

	    /* Simulate a drop in each of the columns and see what the results are. */

	    for (i=0; i<size_x; i++) {
	        push_state();
	        current_column = drop_order[i];

	        /* If this column is full, ignore it as a possibility. */
	        if (drop_piece(real_players, current_column) < 0) {
	            this.current_state = state_stack[--this.depth];
	            continue;
	        }

	        /* If this drop wins the game, take it! */
	        else if (current_state.winner == real_players) {
	            best_column = current_column;
	            this.current_state = state_stack[--this.depth];
	            break;
	        }

	        /* Otherwise, look ahead to see how good this move may turn out */
	        /* to be (assuming the opponent makes the best moves possible). */
	        else {
	           // next_poll = clock() + poll_interval;
	            goodness = evaluate(real_players, level, -(Integer.MAX_VALUE), -best_worst);
	        }

	        /* If this move looks better than the ones previously considered, */
	        /* remember it.                                                   */
	        if (goodness > best_worst) {
	            best_worst = goodness;
	            best_column = current_column;
	            num_of_equal = 1;
	        }

	        /* If two moves are equally as good, make a random decision. */
	        else if (goodness == best_worst) {
	            num_of_equal++;
	            if (random.nextInt()%10000 < ((float)1/(float)num_of_equal) * 10000)
	                best_column = current_column;
	        }

	        this.current_state = state_stack[--this.depth];
	    }

	    MOVE_IN_PROGRESS = false;

	    /* Drop the piece in the column decided upon. */

	    if (best_column >= 0) {
	       drop_piece(real_players, best_column);//result=
	        column = best_column;
	        return column;
	    }
	    else
	    {
	    	return column;
	    	//throw exception
	    	  //return FALSE;
	    }
	      
	}
	public byte[][]	c4_board()
	{
	    assert GAME_IN_PROGRESS;
	    return (byte[][]) this.current_state.board;
	}
	public int c4_score_of_player(byte player)
	{
	    assert GAME_IN_PROGRESS;
	    return this.current_state.score[real_player(player)];
	}
	public Boolean c4_is_winner(byte player)
	{
	    assert GAME_IN_PROGRESS;
	    return (this.current_state.winner == real_player(player));
	}
	public boolean c4_is_tie()
	{
	    assert(GAME_IN_PROGRESS);
	    return (this.current_state.num_of_pieces == this.size_x * this.size_y);
	}
	public void c4_reset()
	{
	    assert(!MOVE_IN_PROGRESS);
	    if (GAME_IN_PROGRESS){}
	        // Review this line afterward  - c4_end_game();
	    //poll_function = NULL;
	}
	private int goodness_of(byte player)
    {
		return current_state.score[player] - current_state.score[other(player)];
    }
	private int drop_piece(byte player, int column)
	{
	    int y = 0;
	    //Find position to drop
	    while (current_state.board[column][y] !=(char)C4_NONE && ++y < size_y);
	    //If reached maximum 
	    if(y==this.size_y)
	    	return -1;
	    //place players piece in feild
	    this.current_state.board[column][y] = player;
	    this.current_state.num_of_pieces++;
	    //update score of player
	    update_score(player, column, y);

	    return y;
	}
	private byte real_player(byte x)
	{
		return (byte) ((x) & 1);
	}
	private byte other(byte x)
	{
		return (byte) ((x)^1);
	}
	private void update_score(byte player, int x, int y)
	{
	    int i;
	    int win_index;
	    int this_difference = 0, other_difference = 0;
	    int[][] current_score_array;
	    current_score_array= this.current_state.score_array;
	    byte other_player = other(player);

	    for (i=0; map[x][y][i] != -1; i++) {
	        win_index = map[x][y][i];
	        this_difference += current_score_array[player][win_index];
	        other_difference += current_score_array[other_player][win_index];

	        current_score_array[player][win_index] <<= 1;
	        current_score_array[other_player][win_index] = 0;

	        if (current_score_array[player][win_index] == magic_win_number)
	            if (this.current_state.winner == C4_NONE)
	                current_state.winner = player;
	    }

	    current_state.score[player] += this_difference;
	    current_state.score[other_player] -= other_difference;
	}
	private int num_of_win_plcaes(int x,int y,int n) {
		
		if (x < n && y < n)
	        return 0;
	    else if (x < n)
	        return x * ((y-n)+1);
	    else if (y < n)
	        return y * ((x-n)+1);
	    else
	        return 4*x*y - 3*x*n - 3*y*n + 3*x + 3*y - 4*n + 2*n*n + 2;
	}
	private void push_state()
	{
	    int i;
	    Game_state old_state, new_state;

	    //win_places_array_size = win_places * sizeof(int);
	    old_state = state_stack[depth++];
	    new_state = state_stack[depth];

	    if (depth == states_allocated) {

	        /* Allocate space for the board */
	    	new_state=new Game_state(this.size_x,this.size_y,this.win_places);
	        states_allocated++;
	    }

	    /* Copy the board */

	    for (i=0; i<size_x; i++)
	        System.arraycopy(old_state.board[i], 0, new_state.board[i],0, this.size_y);

	    /* Copy the score array */

	    System.arraycopy(old_state.score_array[0],0,new_state.score_array[0],0,this.win_places);
	    System.arraycopy(old_state.score_array[1], 0,new_state.score_array[1], 0, this.win_places);

	    new_state.score[0] = old_state.score[0];
	    new_state.score[1] = old_state.score[1];
	    new_state.winner = old_state.winner;

	    current_state = new_state;
	}
	private int	evaluate(byte player, int level, int alpha, int beta)
	{
	    int i, goodness, best, maxab;

	  /*  if (poll_function && next_poll <= clock()) {
	        next_poll += poll_interval;
	        (*poll_function)();
	    }
	   */
	    if (level == depth)
	        return goodness_of(player);
	    else {
	        /* Assume it is the other player's turn. */
	        best = -(Integer.MAX_VALUE);
	        maxab = alpha;
	        for(i=0; i<size_x; i++) {
	            push_state();
	            if (drop_piece(other(player), drop_order[i]) < 0) {
	                this.current_state = state_stack[--this.depth];
	                continue;
	            }
	            else if (current_state.winner == other(player))
	                goodness = Integer.MAX_VALUE - depth;
	            else
	                goodness = evaluate(other(player), level, -beta, -maxab);
	            if (goodness > best) {
	                best = goodness;
	                if (best > maxab)
	                    maxab = best;
	            }
	            this.current_state = state_stack[--this.depth];
	            if (best > beta)
	                break;
	        }

	        /* What's good for the other player is bad for this one. */
	        return -best;
	    }
	}
	public void draw()
	{
		System.out.println();
		for(int i=size_y-1;i>=0;i--)
		{
			for(int j=0;j<this.size_x;j++)
				System.out.print(this.current_state.board[j][i]+" ");
			System.out.print("\n");
			
		}
		
	}

}

