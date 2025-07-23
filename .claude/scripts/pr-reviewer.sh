#!/bin/bash

function review-pr() {
    local pr_number=""
    local detailed=false
    local focus_area=""
    local repo_owner=$(git config --get remote.origin.url | sed 's/.*github.com[:/]\([^/]*\).*/\1/')
    local repo_name=$(git config --get remote.origin.url | sed 's/.*\/\([^.]*\)\.git/\1/' | sed 's/.*\/\([^/]*\)$/\1/')
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --detailed)
                detailed=true
                shift
                ;;
            --focus)
                focus_area="$2"
                shift 2
                ;;
            [0-9]*)
                pr_number="$1"
                shift
                ;;
            *)
                echo "Unknown option: $1"
                return 1
                ;;
        esac
    done
    
    if [ -z "$pr_number" ]; then
        echo "❌ Error: PR number is required"
        echo "Usage: /reviewpr <pr_number> [--detailed] [--focus <area>]"
        echo "Focus areas: security, performance, architecture, testing, documentation"
        return 1
    fi
    
    echo "🔍 Reviewing Pull Request #$pr_number"
    echo "📊 Repository: $repo_owner/$repo_name"
    
    # Step 1: Fetch PR details
    echo "📥 Step 1: Fetching PR details..."
    local pr_data=$(gh pr view $pr_number --json title,body,labels,author,reviewRequests,files,additions,deletions,commits)
    local pr_title=$(echo "$pr_data" | jq -r '.title')
    local pr_body=$(echo "$pr_data" | jq -r '.body')
    local pr_author=$(echo "$pr_data" | jq -r '.author.login')
    local pr_additions=$(echo "$pr_data" | jq -r '.additions')
    local pr_deletions=$(echo "$pr_data" | jq -r '.deletions')
    local pr_files=$(echo "$pr_data" | jq -r '.files[].path' | tr '\n' ' ')
    
    echo "📋 Title: $pr_title"
    echo "👤 Author: $pr_author"
    echo "📊 Changes: +$pr_additions -$pr_deletions"
    echo "📁 Files: $pr_files"
    
    # Step 2: Get diff
    echo ""
    echo "📝 Step 2: Fetching code changes..."
    local pr_diff=$(gh pr diff $pr_number)
    
    # Step 3: AI Review
    echo ""
    echo "🤖 Step 3: AI Code Review Analysis..."
    echo "========================= PR REVIEW SESSION ========================="
    
    local focus_instruction=""
    if [ -n "$focus_area" ]; then
        focus_instruction="SPECIAL FOCUS: Pay extra attention to $focus_area aspects of this code."
    fi
    
    local detailed_instruction=""
    if [ "$detailed" = true ]; then
        detailed_instruction="DETAILED REVIEW: Provide comprehensive analysis including architectural implications, maintainability, and long-term considerations."
    fi
    
    claude-code "Please perform a thorough code review of Pull Request #$pr_number from $repo_owner/$repo_name.

PR DETAILS:
Title: $pr_title
Author: $pr_author
Description: $pr_body
Files changed: $pr_files
Lines: +$pr_additions -$pr_deletions

$focus_instruction
$detailed_instruction

FULL DIFF:
$pr_diff

Please provide a comprehensive code review covering:

1. **CODE QUALITY ANALYSIS**
   - Code style and consistency
   - Best practices adherence
   - Readability and maintainability
   - Code organization and structure

2. **FUNCTIONAL REVIEW**
   - Does the code do what the PR claims?
   - Are there any logical errors or bugs?
   - Edge cases and error handling
   - Input validation and sanitization

3. **SECURITY ANALYSIS**
   - Security vulnerabilities
   - Authentication/authorization issues
   - Data exposure risks
   - Injection attack prevention

4. **PERFORMANCE CONSIDERATIONS**
   - Performance implications
   - Resource usage (memory, CPU, I/O)
   - Scalability concerns
   - Optimization opportunities

5. **TESTING ASSESSMENT**
   - Test coverage adequacy
   - Test quality and completeness
   - Missing test scenarios
   - Integration testing needs

6. **ARCHITECTURE & DESIGN**
   - Design pattern usage
   - Architecture consistency
   - Separation of concerns
   - SOLID principles adherence

7. **DOCUMENTATION & COMMENTS**
   - Code documentation quality
   - API documentation updates
   - README or wiki updates needed
   - Comment clarity and usefulness

8. **COMPATIBILITY & INTEGRATION**
   - Backward compatibility
   - Breaking changes identification
   - Integration with existing systems
   - Database migration needs

9. **SPECIFIC FEEDBACK**
   - Line-by-line specific suggestions
   - Refactoring recommendations
   - Alternative implementation approaches
   - Code examples for improvements

10. **OVERALL ASSESSMENT**
    - Recommendation: APPROVE / REQUEST CHANGES / COMMENT
    - Priority of issues found (Critical/High/Medium/Low)
    - Summary of main concerns
    - Positive aspects worth highlighting

Please be thorough but constructive in your feedback."
    
    echo ""
    echo "📋 Step 4: Review Summary and Action Items..."
    
    read -p "📝 Based on the AI review above, what's your recommendation? (approve/changes/comment): " recommendation
    
    case $recommendation in
        approve|a)
            echo "✅ Approving PR #$pr_number..."
            gh pr review $pr_number --approve --body "AI-assisted review completed. Code looks good! ✅

$(echo "Key review points covered:
- Code quality and best practices
- Security considerations  
- Performance implications
- Testing adequacy
- Documentation completeness

Ready for merge! 🚀")"
            ;;
        changes|c)
            echo "⚠️  Requesting changes for PR #$pr_number..."
            echo "Please provide specific feedback based on the AI review above:"
            read -p "Enter your change requests: " change_feedback
            gh pr review $pr_number --request-changes --body "AI-assisted review identified areas for improvement 🔍

$change_feedback

Please address these concerns and re-request review when ready."
            ;;
        comment|*)
            echo "💬 Adding review comments to PR #$pr_number..."
            read -p "Enter your review comments: " review_comments
            gh pr review $pr_number --comment --body "AI-assisted code review feedback 🤖

$review_comments

Let me know if you have any questions about these suggestions!"
            ;;
    esac
    
    echo ""
    echo "🎉 PR Review completed!"
    echo "🔗 View PR: https://github.com/$repo_owner/$repo_name/pull/$pr_number"
    echo ""
    echo "Suggested follow-up actions:"
    echo "  • Monitor PR for author responses"
    echo "  • Re-review after changes are made"
    echo "  • Merge when ready and approved"
}

# Make the function available
alias reviewpr='review-pr'
