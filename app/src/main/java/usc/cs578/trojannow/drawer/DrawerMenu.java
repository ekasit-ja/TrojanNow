package usc.cs578.trojannow.drawer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import usc.cs578.com.trojannow.R;


public class DrawerMenu extends Fragment {

	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private View mainView;
	private ListView listView;
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	public DrawerMenu() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment DrawerMenu.
	 */
	// TODO: Rename and change types and number of parameters
	public static DrawerMenu newInstance(String param1, String param2) {
		DrawerMenu fragment = new DrawerMenu();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.drawer_menu, container, false);
	}

	// TODO: Rename method, updateListView argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public void setUp(int mainViewId, DrawerLayout drawerLayout, Toolbar toolbar) {
		// set listView of menu item
		DrawerMenuAdapter adapter = new DrawerMenuAdapter(getActivity(), drawerLayout);
		listView = (ListView) getActivity().findViewById(R.id.menu_listView);
		listView.setAdapter(adapter);

		// set up drawer
		mainView = getActivity().findViewById(mainViewId);
		mDrawerLayout = drawerLayout;
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// make main page slide to the left when drawer is opened
				super.onDrawerSlide(drawerView, slideOffset);
				mainView.setTranslationX(slideOffset * -1 * drawerView.getWidth());
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	// implement this interface on class that use this drawer
	public interface OnFragmentInteractionListener {
		public void onFragmentInteraction(Uri uri);
	}

}
