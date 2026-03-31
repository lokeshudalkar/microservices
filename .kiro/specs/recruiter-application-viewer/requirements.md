# Requirements Document

## Introduction

This feature enables recruiters to view and manage job applications for their posted jobs directly from the header navigation. Currently, job seekers can view their applications via "My Applications" in the header, but recruiters lack a similar convenient way to access applications for their job postings. This feature will add a "My Job Postings" navigation item in the header for recruiters, allowing them to view their jobs and the applications submitted for each specific job.

## Glossary

- **Recruiter**: A user with the RECRUITER role who can post jobs and view applications
- **Job_Seeker**: A user with the SEEKER role who can apply to jobs
- **Job_Posting**: A job advertisement created by a recruiter
- **Application**: A job application submitted by a job seeker for a specific job posting
- **Header**: The top navigation bar of the application containing the logo and navigation menu
- **Application_Viewer**: The UI component that displays applications for a specific job posting
- **Job_List**: A list of all job postings created by the authenticated recruiter
- **Frontend**: The React-based user interface (jobPortal_frontend)
- **Backend**: The microservices architecture including jobservice and ApplicationService
- **API_Gateway**: The entry point for all API requests that routes to appropriate microservices

## Requirements

### Requirement 1: Header Navigation for Recruiters

**User Story:** As a recruiter, I want to see a "My Job Postings" link in the header navigation, so that I can quickly access my posted jobs and their applications.

#### Acceptance Criteria

1. WHEN a user with RECRUITER role is authenticated, THE Frontend SHALL display a "My Job Postings" navigation item in the header
2. WHEN a user with SEEKER role is authenticated, THE Frontend SHALL NOT display the "My Job Postings" navigation item
3. WHEN a recruiter clicks the "My Job Postings" link, THE Frontend SHALL navigate to the job postings page
4. THE Frontend SHALL display the "My Job Postings" link in the same navigation area as the existing "My Applications" link for seekers

### Requirement 2: Job Postings List Page

**User Story:** As a recruiter, I want to view a list of all my job postings, so that I can select a specific job to view its applications.

#### Acceptance Criteria

1. WHEN a recruiter navigates to the job postings page, THE Frontend SHALL fetch all job postings created by that recruiter from the Backend
2. WHEN the job postings are loaded, THE Frontend SHALL display each job posting with its title, company name, location, and application count
3. WHEN a recruiter clicks on a job posting, THE Frontend SHALL navigate to the applications view for that specific job
4. WHEN no job postings exist for the recruiter, THE Frontend SHALL display a message indicating no jobs have been posted
5. WHEN the API request fails, THE Frontend SHALL display an appropriate error message to the user

### Requirement 3: Application List for Specific Job

**User Story:** As a recruiter, I want to view all applications submitted for a specific job posting, so that I can review candidates who applied.

#### Acceptance Criteria

1. WHEN a recruiter selects a specific job posting, THE Frontend SHALL fetch all applications for that job from the Backend
2. WHEN applications are loaded, THE Frontend SHALL display each application with the applicant's email, resume file name, and application submission date
3. WHEN no applications exist for the job, THE Frontend SHALL display a message indicating no applications have been received
4. WHEN the API request fails, THE Frontend SHALL display an appropriate error message to the user
5. THE Frontend SHALL display the job title and company name at the top of the applications list for context

### Requirement 4: Resume Download Functionality

**User Story:** As a recruiter, I want to download applicant resumes, so that I can review candidate qualifications in detail.

#### Acceptance Criteria

1. WHEN viewing an application, THE Frontend SHALL display a download button or link for the resume
2. WHEN a recruiter clicks the download button, THE Frontend SHALL initiate a download of the resume file
3. WHEN the resume file is not available, THE Frontend SHALL display an appropriate error message
4. THE Frontend SHALL preserve the original filename of the uploaded resume during download

### Requirement 5: Backend API for Recruiter Job Postings

**User Story:** As a system, I want to provide an API endpoint for retrieving recruiter job postings, so that the frontend can display them.

#### Acceptance Criteria

1. THE Backend SHALL provide a GET endpoint that returns all job postings for the authenticated recruiter
2. WHEN a request is made with valid recruiter credentials, THE Backend SHALL return a list of job postings with id, title, company name, location, salary, and application count
3. WHEN a request is made without authentication, THE Backend SHALL return a 401 Unauthorized response
4. WHEN a request is made by a non-recruiter user, THE Backend SHALL return a 403 Forbidden response
5. THE Backend SHALL use the X-User-Email and X-User-Role headers to identify the authenticated recruiter

### Requirement 6: Backend API for Job Applications

**User Story:** As a system, I want to provide an API endpoint for retrieving applications for a specific job, so that recruiters can view candidate submissions.

#### Acceptance Criteria

1. THE Backend SHALL provide a GET endpoint that returns all applications for a specific job posting
2. WHEN a recruiter requests applications for their own job posting, THE Backend SHALL return a list of applications with seeker email, resume path, and submission timestamp
3. WHEN a recruiter requests applications for a job they do not own, THE Backend SHALL return a 403 Forbidden response
4. WHEN a request is made without authentication, THE Backend SHALL return a 401 Unauthorized response
5. WHEN the job posting does not exist, THE Backend SHALL return a 404 Not Found response
6. THE Backend SHALL verify that the requesting recruiter is the owner of the job posting before returning applications

### Requirement 7: Resume File Access

**User Story:** As a system, I want to provide secure access to resume files, so that recruiters can download applicant resumes.

#### Acceptance Criteria

1. THE Backend SHALL provide a GET endpoint for downloading resume files by application ID
2. WHEN a recruiter requests a resume for an application on their job posting, THE Backend SHALL return the resume file with appropriate content type headers
3. WHEN a recruiter requests a resume for an application not on their job posting, THE Backend SHALL return a 403 Forbidden response
4. WHEN the resume file does not exist, THE Backend SHALL return a 404 Not Found response
5. THE Backend SHALL set the Content-Disposition header to prompt file download with the original filename

### Requirement 8: Authorization and Security

**User Story:** As a system administrator, I want to ensure only authorized recruiters can view applications, so that candidate data remains secure.

#### Acceptance Criteria

1. THE Backend SHALL validate that the authenticated user has the RECRUITER role before allowing access to job applications
2. THE Backend SHALL verify that the recruiter owns the job posting before returning its applications
3. WHEN authorization fails, THE Backend SHALL return appropriate HTTP status codes (401 for unauthenticated, 403 for unauthorized)
4. THE Backend SHALL log all access attempts to application data for security auditing
5. THE Frontend SHALL redirect unauthenticated users to the login page when accessing recruiter-only pages

### Requirement 9: User Experience and Navigation

**User Story:** As a recruiter, I want a smooth and intuitive navigation experience, so that I can efficiently review applications.

#### Acceptance Criteria

1. WHEN viewing the job postings list, THE Frontend SHALL provide a back button or link to return to the home page
2. WHEN viewing applications for a specific job, THE Frontend SHALL provide a back button or link to return to the job postings list
3. THE Frontend SHALL display loading indicators while fetching data from the Backend
4. THE Frontend SHALL maintain consistent styling with the existing application design
5. WHEN navigation occurs, THE Frontend SHALL update the browser URL to allow direct linking and browser back/forward navigation
