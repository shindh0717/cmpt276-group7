Meeting #2 
Date: June 28, 2026
Time: 2:00 PM - 3:20 PM
Platform: Discord (voice channel)
Attendees:
- Andrew TJ
- shindh0717
- jaspreetS1ngh
- Aveer

Agenda Items
- Define Iteration 1 scope and deliverables
- Break down features into individual tasks
- Assign responsibilities to team members
- Set deadlines and milestones
- Establish Git workflow for collaboration

1. Iteration 1 Scope

The group finalized the following features for Iteration 1 based on the project requirements:

    1. UI/UX Design (Mockups)
        Create wireframes and mockups for all pages including login, registration, admin dashboard, user profile, and the extra feature.
        This ensures the team has a clear visual direction before coding begins.

    2. User Registration
        Users can create an account with email and password.
        Passwords must be hashed before storing in the database.
        All new users default to the 'regular' role.
        Handles duplicate email errors and weak password validation.

    3. User Login/Logout
        Users can log in with their credentials.
        Session management to keep users logged in.
        Logout functionality to destroy session.
        Redirect users based on their role (Admin vs Regular).
        Handles invalid credentials and non-existent users.

    4. Role-Based Views
        Admin users can view all registered users in the database.
        Regular users can only view their own profile information.
        Middleware to block unauthorized access to protected routes.
        Regular users cannot access admin pages even if they type the URL.

    5. Extra Feature: Making the Map
        Integrate Google Maps API into the application.
        Display an interactive map on the user dashboard.
        Users can view and navigate the map after logging in.
        Map should be centered on a default location (e.g., Vancouver).
        Basic map controls (zoom, pan, etc.) should be functional.
        This serves as the foundation for future location-based features.


2. Task Distribution for Iteration 1

    1. UI/UX Design (Mockups)
        Assigned to: asiandude121
        Deliverables: Working registration system by July 5

    2. User Registration
        Assigned to: Aveer
        Deliverables: Working registration system by July 5

    3. User Login/Logout
        Assigned to: jaspreetS1ngh
        Deliverables: Working authentication system by July 5

    4. Role-Based Views
        Assigned to: Andrew TJ
        Deliverables: Admin and Regular user dashboards by July 5

    5. Extra Feature: Making the Map
        Assigned to: shindh0717
        Deliverables: Google Maps integration with working map display by July 5


3. Git Workflow Agreement

The group agreed on the following Git workflow to manage collaboration:

    1. Branch Structure:
        - main: Production-ready code
        - feature/design (asiandude121)
        - feature/registration (aveer)
        - feature/login (jaspreetS1ngh)
        - feature/roles (andrewtj)
        - feature/save-locations (shindh0717)

    2. Process:
        - Create branch from main for your feature
        - Commit regularly with clear messages
        - Push branch to GitHub
        - Create Pull Request (PR) to merge into main
        - At least 1 team member must review and approve PR
        - No direct pushes to main branch

    3. Commit Message Format:
        - feat: New feature
        - fix: Bug fix
        - docs: Documentation changes
        - test: Adding tests


6. Success Criteria for Iteration 1

The group defined the following success criteria for Iteration 1:

    Functional Requirements:
        - Users can register with email and password
        - Users can log in and log out
        - Admin users see all users in database
        - Regular users see only their own profile
        - Users can save locations to personal list
        - Unauthorized access is blocked

    Documentation Requirements:
        - User stories documented (Persona, Action, Result)
        - Test cases for success and failure scenarios
        - UI mockups for all pages
        - README updated with setup instructions
        - Screencast showing features on Render (not localhost)


Next Meeting: July 5, 2026 at around 2:00 - 3:00 PM (Progress Check-in)
