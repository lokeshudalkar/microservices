# Implementation Plan: Recruiter Application Viewer

## Overview

This implementation plan breaks down the recruiter application viewer feature into discrete coding tasks. The tasks are organized to build incrementally, starting with backend endpoints, then frontend components, and finally integration and testing. Each task builds on previous work to ensure no orphaned code.

## Tasks

- [ ] 1. Set up backend resume download endpoint
  - [ ] 1.1 Create ApplicationResponseDto with seeker email field
    - Create new DTO class in `ApplicationService/src/main/java/com/jobportal/ApplicationService/JobApplicationDto/ApplicationResponseDto.java`
    - Include fields: id, seekerId, seekerEmail, jobId, resumePath, appliedAt
    - Add getters, setters, and constructors
    - _Requirements: 6.2_

  - [ ] 1.2 Add loadFileAsResource method to FileStorageService
    - Modify `ApplicationService/src/main/java/com/jobportal/ApplicationService/JobApplicationService/FileStorageService.java`
    - Implement method to load file from file system as Spring Resource
    - Handle IOException for missing files
    - Validate file paths to prevent directory traversal
    - _Requirements: 7.2, 7.4_

  - [ ] 1.3 Create resume download endpoint in JobApplicationController
    - Add GET endpoint `/download-resume/{applicationId}` in `ApplicationService/src/main/java/com/jobportal/ApplicationService/JobApplicationController/JobApplicationController.java`
    - Validate RECRUITER role from X-User-Role header
    - Fetch application by ID from repository
    - Verify recruiter owns the job (via Feign call to jobservice)
    - Load resume file using FileStorageService
    - Set Content-Type and Content-Disposition headers
    - Return file as Resource
    - Handle errors: 403 for unauthorized, 404 for not found
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2_

  - [ ]* 1.4 Write property test for resume download authorization
    - **Property 14: Job Ownership Verification**
    - **Validates: Requirements 6.6, 8.2**
    - Generate random recruiter/job/application combinations
    - Verify only job owners can download resumes
    - Verify 403 response for non-owners

  - [ ]* 1.5 Write unit tests for resume download endpoint
    - Test successful download with correct headers
    - Test 403 response when recruiter doesn't own job
    - Test 404 response when application not found
    - Test 404 response when file not found
    - _Requirements: 7.2, 7.3, 7.4, 7.5_

- [ ] 2. Enhance application response with seeker email
  - [ ] 2.1 Add Feign client method to fetch user by ID
    - Modify `ApplicationService/src/main/java/com/jobportal/ApplicationService/FeignClient/UserClient.java`
    - Add method to get user details by seeker ID
    - Handle circuit breaker for service unavailability
    - _Requirements: 6.2_

  - [ ] 2.2 Create service method to map applications to response DTOs
    - Add method in `ApplicationService/src/main/java/com/jobportal/ApplicationService/JobApplicationService/JobApplicationService.java`
    - Fetch seeker emails for each application using UserClient
    - Map JobApplication entities to ApplicationResponseDto objects
    - Handle cases where user service is unavailable
    - _Requirements: 6.2_

  - [ ] 2.3 Update getApplicationsForJob endpoint to return enhanced DTOs
    - Modify the Feign client endpoint in `jobservice/src/main/java/com/jobportal/jobservice/feignClient/JobApplicationClient.java` to return ApplicationResponseDto list
    - Update the controller in jobservice to use the enhanced response
    - _Requirements: 6.2_

  - [ ]* 2.4 Write property test for application response structure
    - **Property 13: Backend Response Structure - Applications**
    - **Validates: Requirements 6.2**
    - Generate random application data
    - Verify all required fields present in response (seeker email, resume path, timestamp)

- [ ] 3. Implement frontend navigation for recruiters
  - [ ] 3.1 Add "My Job Postings" link to NavBar component
    - Modify `jobPortal_frontend/src/components/navBar.jsx`
    - Import `isRecruiter` function from auth utils
    - Add conditional rendering for recruiter navigation item
    - Link to `/my-job-postings` route
    - _Requirements: 1.1, 1.2, 1.3_

  - [ ]* 3.2 Write property test for role-based navigation visibility
    - **Property 1: Role-based Navigation Visibility**
    - **Validates: Requirements 1.1, 1.2**
    - Generate random user roles (RECRUITER, SEEKER)
    - Verify navigation item visibility matches role
    - Test with authenticated and unauthenticated states

  - [ ]* 3.3 Write unit tests for NavBar component
    - Test "My Job Postings" link appears for recruiters
    - Test link does not appear for seekers
    - Test link navigation to correct route
    - _Requirements: 1.1, 1.2, 1.3_

- [ ] 4. Create MyJobPostings page component
  - [ ] 4.1 Create MyJobPostings component with state management
    - Create `jobPortal_frontend/src/pages/MyJobPostings.jsx`
    - Set up state for jobPostings, loading, and error
    - Implement useEffect to fetch job postings on mount
    - Use getAuthHeaders() for authentication
    - Fetch from `/jobs/my-jobs` endpoint via API gateway
    - _Requirements: 2.1, 5.1_

  - [ ] 4.2 Implement job postings list rendering
    - Display loading indicator while fetching
    - Render job postings in grid or list format
    - Show title, company name, location, and application count for each job
    - Make each job posting clickable with navigation to `/my-job-postings/{jobId}/applications`
    - Display empty state message when no jobs exist
    - Display error message on API failure
    - _Requirements: 2.2, 2.3, 2.4, 2.5_

  - [ ] 4.3 Create CSS styling for MyJobPostings
    - Create `jobPortal_frontend/src/pages/MyJobPostings.css`
    - Style job postings grid/list layout
    - Style loading and error states
    - Ensure responsive design
    - Maintain consistency with existing application styles
    - _Requirements: 9.4_

  - [ ] 4.4 Add back navigation to home page
    - Add back button or link to return to home page
    - _Requirements: 9.1_

  - [ ]* 4.5 Write property test for job posting display completeness
    - **Property 4: Job Posting Display Completeness**
    - **Validates: Requirements 2.2**
    - Generate random job postings
    - Verify all required fields are rendered (title, company, location, count)

  - [ ]* 4.6 Write unit tests for MyJobPostings component
    - Test loading state display
    - Test job postings rendering with mock data
    - Test empty state display
    - Test error state display
    - Test navigation on job posting click
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ] 5. Create JobApplications page component
  - [ ] 5.1 Create JobApplications component with state management
    - Create `jobPortal_frontend/src/pages/JobApplications.jsx`
    - Set up state for applications, jobInfo, loading, and error
    - Extract jobId from URL params using useParams hook
    - Implement useEffect to fetch applications on mount
    - Fetch from `/jobs/{jobId}/applications` endpoint
    - _Requirements: 3.1, 6.1_

  - [ ] 5.2 Implement applications list rendering
    - Display job title and company name at top
    - Display loading indicator while fetching
    - Render applications in table format
    - Show columns: Applicant Email, Resume, Applied Date
    - Add download button for each resume
    - Display empty state message when no applications exist
    - Display error message on API failure
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 4.1_

  - [ ] 5.3 Implement resume download functionality
    - Add click handler for download buttons
    - Make request to `/job-applications/download-resume/{applicationId}` endpoint
    - Handle file download with correct filename
    - Display error message if download fails
    - _Requirements: 4.2, 4.3, 4.4_

  - [ ] 5.4 Create CSS styling for JobApplications
    - Create `jobPortal_frontend/src/pages/JobApplications.css`
    - Style applications table layout
    - Style download buttons
    - Style loading and error states
    - Ensure responsive design
    - _Requirements: 9.4_

  - [ ] 5.5 Add back navigation to job postings list
    - Add back button or link to return to `/my-job-postings`
    - _Requirements: 9.2_

  - [ ]* 5.6 Write property test for application display completeness
    - **Property 7: Application Display Completeness**
    - **Validates: Requirements 3.2**
    - Generate random applications
    - Verify all required fields are rendered (email, resume, date)

  - [ ]* 5.7 Write unit tests for JobApplications component
    - Test loading state display
    - Test applications rendering with mock data
    - Test empty state display
    - Test error state display
    - Test download button functionality
    - Test back navigation
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.3_

- [ ] 6. Add routing for new pages
  - [ ] 6.1 Update App.jsx with new routes
    - Modify `jobPortal_frontend/src/App.jsx`
    - Import MyJobPostings and JobApplications components
    - Add route for `/my-job-postings`
    - Add route for `/my-job-postings/:jobId/applications`
    - _Requirements: 1.3, 2.3, 9.5_

  - [ ]* 6.2 Write property test for URL routing
    - **Property 22: URL Routing**
    - **Validates: Requirements 9.5**
    - Generate random navigation actions
    - Verify URL updates correctly
    - Test browser back/forward navigation

- [ ] 7. Implement authentication and authorization guards
  - [ ] 7.1 Add authentication check to recruiter pages
    - Add useEffect in MyJobPostings to check authentication
    - Redirect to login if not authenticated
    - Add same check to JobApplications component
    - _Requirements: 8.5_

  - [ ]* 7.2 Write property test for unauthenticated access redirect
    - **Property 19: Unauthenticated Access Redirect**
    - **Validates: Requirements 8.5**
    - Test access to recruiter pages without authentication
    - Verify redirect to login page occurs

  - [ ]* 7.3 Write unit tests for authentication guards
    - Test redirect on unauthenticated access
    - Test page loads for authenticated recruiters
    - _Requirements: 8.5_

- [ ] 8. Add security audit logging to backend
  - [ ] 8.1 Implement logging for application access
    - Add logging in JobApplicationController for getJobApplications endpoint
    - Log user email, job ID, and timestamp
    - Add logging in resume download endpoint
    - Log user email, application ID, and timestamp
    - _Requirements: 8.4_

  - [ ]* 8.2 Write property test for security audit logging
    - **Property 18: Security Audit Logging**
    - **Validates: Requirements 8.4**
    - Generate random access attempts
    - Verify log entries are created with correct information

- [ ] 9. Checkpoint - Ensure all tests pass
  - Run all unit tests and property tests
  - Verify frontend components render correctly
  - Verify backend endpoints respond correctly
  - Test authentication and authorization flows
  - Ensure all tests pass, ask the user if questions arise

- [ ] 10. Integration testing and final wiring
  - [ ]* 10.1 Write integration test for recruiter views job postings flow
    - Authenticate as recruiter
    - Navigate to My Job Postings
    - Verify job postings are fetched and displayed
    - Verify all job posting fields are present
    - _Requirements: 1.1, 1.3, 2.1, 2.2, 5.1, 5.2_

  - [ ]* 10.2 Write integration test for recruiter views applications flow
    - Authenticate as recruiter
    - Navigate to My Job Postings
    - Click on a job posting
    - Verify applications are fetched and displayed
    - Verify all application fields are present
    - _Requirements: 2.3, 3.1, 3.2, 6.1, 6.2_

  - [ ]* 10.3 Write integration test for recruiter downloads resume flow
    - Authenticate as recruiter
    - Navigate to applications for a job
    - Click download button
    - Verify file download is initiated
    - Verify file content and filename are correct
    - _Requirements: 4.2, 4.4, 7.1, 7.2, 7.5_

  - [ ]* 10.4 Write integration test for authorization failure flows
    - Attempt to access recruiter pages as seeker (verify 403)
    - Attempt to view another recruiter's applications (verify 403)
    - Attempt to download resume for another recruiter's job (verify 403)
    - _Requirements: 5.4, 6.3, 7.3, 8.1, 8.2, 8.3_

  - [ ]* 10.5 Write integration test for error handling flows
    - Simulate API failures (verify error messages)
    - Request non-existent job (verify 404 handling)
    - Request non-existent application (verify 404 handling)
    - _Requirements: 2.5, 3.4, 6.5, 7.4_

- [ ] 11. Final checkpoint - Ensure all tests pass
  - Run complete test suite (unit, property, integration)
  - Verify all features work end-to-end
  - Test in different browsers
  - Verify responsive design on mobile devices
  - Ensure all tests pass, ask the user if questions arise

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties with minimum 100 iterations
- Unit tests validate specific examples and edge cases
- Integration tests verify end-to-end flows
- All tests should be tagged with: **Feature: recruiter-application-viewer, Property {number}: {property_text}**
