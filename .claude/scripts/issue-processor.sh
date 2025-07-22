#!/bin/bash

function process-issue() {
    local issue_number=""
    local branch_name=""
    local skip_tests=false
    local repo_owner=$(git config --get remote.origin.url | sed 's/.*github.com[:/]\([^/]*\).*/\1/')
    local repo_name=$(git config --get remote.origin.url | sed 's/.*\/\([^.]*\)\.git/\1/' | sed 's/.*\/\([^/]*\)$/\1/')
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --branch)
                branch_name="$2"
                shift 2
                ;;
            --no-test)
                skip_tests=true
                shift
                ;;
            [0-9]*)
                issue_number="$1"
                shift
                ;;
            *)
                echo "Unknown option: $1"
                return 1
                ;;
        esac
    done
    
    if [ -z "$issue_number" ]; then
        echo "❌ Error: Issue number is required"
        echo "Usage: /issue <issue_number> [--branch <branch_name>] [--no-test]"
        return 1
    fi
    
    echo "🎯 Processing GitHub Issue #$issue_number"
    echo "📊 Repository: $repo_owner/$repo_name"
    
    # Step 1: Fetch issue details
    echo "📥 Step 1: Fetching issue details from GitHub..."
    local issue_data=$(gh issue view $issue_number --json title,body,labels,assignees,state)
    local issue_title=$(echo "$issue_data" | jq -r '.title')
    local issue_body=$(echo "$issue_data" | jq -r '.body')
    local issue_labels=$(echo "$issue_data" | jq -r '.labels[].name' | tr '\n' ', ')
    
    echo "📋 Issue: $issue_title"
    echo "🏷️  Labels: $issue_labels"
    
    # Step 2: Planning phase with Claude Code
    echo ""
    echo "🧠 Step 2: Planning Phase - Breaking down issue into atomic tasks..."
    echo "======================== SCRATCHPAD SESSION ========================"
    
    claude-code "I need to process GitHub Issue #$issue_number from repository $repo_owner/$repo_name.
    
ISSUE DETAILS:
Title: $issue_title
Body: $issue_body
Labels: $issue_labels

Please help me plan this work by:

1. ANALYSIS: Analyze the issue and understand what needs to be done
2. BREAKDOWN: Break this issue down into small, atomic tasks that can be implemented independently
3. DEPENDENCIES: Identify any dependencies between tasks
4. IMPLEMENTATION ORDER: Suggest the order in which tasks should be implemented
5. TESTING STRATEGY: Recommend how each task should be tested
6. FILES TO MODIFY: Identify which files will likely need to be created/modified
7. POTENTIAL RISKS: Flag any potential risks or edge cases to consider

Please provide a detailed scratchpad-style breakdown that I can follow step by step."
    
    read -p "📝 Review the planning output above. Continue with implementation? (y/n): " continue_impl
    if [[ $continue_impl != "y" && $continue_impl != "Y" ]]; then
        echo "⏸️  Issue processing paused. You can restart with: /issue $issue_number"
        return 0
    fi
    
    # Step 3: Create branch
    if [ -z "$branch_name" ]; then
        branch_name="issue-$issue_number-$(echo "$issue_title" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9]/-/g' | sed 's/--*/-/g' | sed 's/^-\|-$//g')"
    fi
    
    echo ""
    echo "🌿 Step 3: Creating branch '$branch_name'..."
    git checkout -b "$branch_name"
    
    # Step 4: Implementation phase
    echo ""
    echo "⚡ Step 4: Implementation Phase..."
    echo "=========================== IMPLEMENTATION ==========================="
    
    claude-code "Now let's implement the solution for GitHub Issue #$issue_number.
    
Based on the planning we just did, please help me implement the code changes needed.

CURRENT PROJECT STRUCTURE (provide context about the repository structure here):
Repository: $repo_owner/$repo_name
Current branch: $branch_name
Current directory: $(pwd)
Project files: $(find . -type f -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.properties" | grep -v target | head -20)

Please provide:
1. Specific code changes needed
2. New files to create (with full content)
3. Existing files to modify (with exact changes)
4. Configuration updates if needed
5. Any database migrations or schema changes

Implement each atomic task we identified in the planning phase. Let me know what files to create/modify and I'll apply the changes."
    
    read -p "🔨 Review the implementation guidance. Ready to apply changes? (y/n): " continue_changes
    if [[ $continue_changes != "y" && $continue_changes != "Y" ]]; then
        echo "⏸️  Implementation paused. Current branch: $branch_name"
        return 0
    fi
    
    echo "✍️  Apply the code changes as suggested above, then press Enter to continue..."
    read -p "Press Enter when you've made the code changes..."
    
    # Step 5: Testing phase
    if [ "$skip_tests" = false ]; then
        echo ""
        echo "🧪 Step 5: Testing Phase..."
        echo "============================ TESTING ============================"
        
        claude-code "Let's test the implementation for GitHub Issue #$issue_number.

TESTING REQUIREMENTS:
- Issue: $issue_title
- Branch: $branch_name
- Changes made: $(git diff --name-only)

Please help me create a comprehensive testing strategy:

1. UNIT TESTS: What unit tests should be written/updated?
2. INTEGRATION TESTS: What integration scenarios need testing?
3. MANUAL TESTING: What manual test cases should I run?
4. EDGE CASES: What edge cases should be tested?
5. REGRESSION TESTING: What existing functionality should be verified?
6. DEPLOYMENT TESTING: How should this be tested in a deployed environment?

Please provide specific test commands, test cases, and verification steps."
        
        echo "🔬 Running test sweep..."
        # Add your project's test commands here
        if [ -f "pom.xml" ]; then
            echo "☕ Running Maven tests..."
            mvn test
        elif [ -f "build.gradle" ]; then
            echo "🐘 Running Gradle tests..."
            ./gradlew test
        elif [ -f "package.json" ]; then
            echo "📦 Running npm tests..."
            npm test
        fi
        
        read -p "✅ Tests completed. Did all tests pass? (y/n): " tests_passed
        if [[ $tests_passed != "y" && $tests_passed != "Y" ]]; then
            echo "❌ Tests failed. Please fix issues before continuing."
            echo "Current branch: $branch_name"
            return 1
        fi
    fi
    
    # Step 6: Commit changes
    echo ""
    echo "💾 Step 6: Committing changes..."
    echo "Running /commit command..."
    
    # This would call your existing /commit command
    commit
    
    # Step 7: Create PR
    echo ""
    echo "📤 Step 7: Creating Pull Request..."
    
    local pr_title="Fix #$issue_number: $issue_title"
    local pr_body="Resolves #$issue_number

## Changes Made
<!-- Describe what was implemented -->

## Testing
<!-- Describe testing performed -->

## Review Notes
<!-- Any specific areas that need attention during review -->

Fixes #$issue_number"
    
    gh pr create --title "$pr_title" --body "$pr_body" --base main --head "$branch_name"
    
    local pr_number=$(gh pr list --head "$branch_name" --json number --jq '.[0].number')
    
    echo ""
    echo "🎉 Issue #$issue_number processing completed!"
    echo "🌿 Branch: $branch_name"
    echo "📤 Pull Request: #$pr_number"
    echo "🔗 PR URL: https://github.com/$repo_owner/$repo_name/pull/$pr_number"
    echo ""
    echo "Next steps:"
    echo "  1. Review the PR at the URL above"
    echo "  2. Use '/reviewpr $pr_number' to get AI review"
    echo "  3. Address any feedback and merge when ready"
}

# Make the function available
alias issue='process-issue'
